package cc.sanddata.srpc.server;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import cc.sanddata.srpc.comm.SRPCDecoder;
import cc.sanddata.srpc.comm.SRPCEncoder;
import cc.sanddata.srpc.comm.SRPCRequest;
import cc.sanddata.srpc.comm.SRPCResponse;
import cc.sanddata.srpc.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 框架的RPC服务器（用于将用户系统的业务类发布为RPC服务）
 * 使用时，可由用户通过spring-bean的方式注入到用户的业务系统中
 * 本类实现了ApplicationContextAware InitinalzingBean
 * spring在构造本对象时会调用setApplicationContest()方法，从而可以在方法中通过自定义注解获得用户的业务接口和实现
 * 还会调用afterPropertiesSet()方法，在方法中启动netty服务器
 * @ClassName: SRPCService 
 * @Description: 框架的SRPC服务器
 * @author wanggl
 * @date 2017年8月31日 下午11:12:49 
 *
 */
public class SRPCServer implements ApplicationContextAware,InitializingBean{
	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SRPCServer.class);
	
	private String serverAddress;
	
	private ServiceRegistry registry;//zk
	//存储业务系统接口和实现类的实例对象
	private Map<String,Object> handlerMap = new HashMap<String,Object>();
	
	public SRPCServer(String serverAddress) {
		super();
		this.serverAddress = serverAddress;
	}

	public SRPCServer(String serverAddress, ServiceRegistry registry) {
		super();
		this.serverAddress = serverAddress;
		this.registry = registry;
	}

	/**
	 * 在此启动netty服务，绑定handler流水线
	 * 1、接受请求数据进行反序列化获取request对象
	 * 2、根据request中的参数，让RPChandler从handlerMap中找到对应的业务实现，调用制定方法，获取返回结果
	 * 3、将业务调用结果封装到response并序列化发送客户端
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		LOGGER.info("Starting server at ："+serverAddress);
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup,workGroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel channel) throws Exception {
					channel.pipeline()
						.addLast(new SRPCDecoder(SRPCRequest.class))
						.addLast(new SRPCEncoder(SRPCResponse.class))
						.addLast(new SRPCHandler(handlerMap));
					
				}
				
			}).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
			
			//构造方法中传递过来rpc服务器地址和端口
			String[] array = serverAddress.split(":");
			String host = array[0];
			int port = Integer.parseInt(array[1]);
			//绑定RPC服务器
			ChannelFuture future = bootstrap.bind(host, port).sync();
			LOGGER.debug("server started on port {}", port);
			//通过传递过来的zk属性，在zk上注册RPC服务器
			if (registry != null) {
				registry.registry(serverAddress);
			}
			future.channel().closeFuture().sync();
		} catch (Exception e) {
			// TODO: handle exception
		}finally {
			workGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
		
	}

	/**
	 * 通过注解，获取标注了rpc服务注解的业务类，接口济实现都放进map
	 */
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		Map<String,Object> serviceBeanMap = context.getBeansWithAnnotation(SRPCService.class);
		
		if(MapUtils.isNotEmpty(serviceBeanMap)){
			for(Object serviceBean:serviceBeanMap.values()){
				String interfaceName = serviceBean.getClass().getAnnotation(SRPCService.class).value().getName();
				
				handlerMap.put(interfaceName, serviceBean);
			}
		}
		
	}

	
	
}
