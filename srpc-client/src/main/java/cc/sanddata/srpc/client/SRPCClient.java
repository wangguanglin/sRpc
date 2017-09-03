package cc.sanddata.srpc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.sanddata.srpc.comm.SRPCDecoder;
import cc.sanddata.srpc.comm.SRPCEncoder;
import cc.sanddata.srpc.comm.SRPCRequest;
import cc.sanddata.srpc.comm.SRPCResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 
 * @ClassName: SRPCClient 
 * @Description: SRPC框架客户端，继承Netty  SimpleChannelInboundHandler实现
 * @author wanggl
 * @date 2017年8月31日 下午7:54:30 
 *
 */
public class SRPCClient extends SimpleChannelInboundHandler<SRPCResponse> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SRPCClient.class);
	
	private String ip;
	
	private int port;
	
	private SRPCResponse response;
	
	private final Object obj = new Object();

	public SRPCClient(String ip, int port) {
		super();
		this.ip = ip;
		this.port = port;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	/**
	 * 
	 * @Title: send 
	 * @author wanggl
	 * @Description: 链接服务端，发送消息 
	 * @param @param request
	 * @param @return 
	 * @return SRPCResponse
	 * @throws
	 */
	public SRPCResponse send(SRPCRequest request) throws Exception{
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group).channel(NioSocketChannel.class)
				.handler(new ChannelInitializer<SocketChannel>() {
					/**
					 * 向pipeline中添加编码、解码、业务处理的handler
					 */
					@Override
					protected void initChannel(SocketChannel channel) throws Exception {
						channel.pipeline().addLast(new SRPCEncoder(SRPCRequest.class))
							.addLast(new SRPCDecoder(SRPCResponse.class))
							.addLast(SRPCClient.this);
						
					}
				}).option(ChannelOption.SO_KEEPALIVE, true);
			//链接服务器
			ChannelFuture future = bootstrap.connect(ip, port).sync();
			
			//将request对象写入outbundle处理后发出
			future.channel().writeAndFlush(request).sync();
			
			//用线程等待的方式决定是否关闭链接
			//意义是：先在此阻塞，等待获得到服务器的返回后，被唤醒，从而关闭网络链接
			synchronized (obj) {
				obj.wait();
			}
			
			if(response != null){
				future.channel().closeFuture().sync();
			}
			return response;
		} catch (Exception e) {
			group.shutdownGracefully();
		}
		
		
		return null;
		
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, SRPCResponse respone) throws Exception {
		
		this.response = respone;
		
		synchronized (obj) {
			obj.notifyAll();
		}
		
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LOGGER.error("client caught exception",cause);
		ctx.close();
	}
}
