package cc.sanddata.srpc.server;

import java.lang.reflect.Method;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.sanddata.srpc.comm.SRPCRequest;
import cc.sanddata.srpc.comm.SRPCResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class SRPCHandler extends SimpleChannelInboundHandler<SRPCRequest >{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SRPCHandler.class);
	
	private final Map<String,Object> handlerMap;

	public SRPCHandler(Map<String, Object> handlerMap) {
		this.handlerMap = handlerMap;
	}
	
	/**
	 * 
	 * @Title: channelRead0 
	 * @author wanggl
	 * @Description: 接收消息，处理消息，返回结果
	 * @param @param arg0
	 * @param @param arg1
	 * @param @throws Exception 
	 * @return void
	 * @throws
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, SRPCRequest request) throws Exception {
		LOGGER.info("start deal request");
		SRPCResponse response = new SRPCResponse();
		
		response.setRequestId(request.getRequestId());
		
		try {
			//根据request来处理具体的业务调度
			Object result = handler(request);
			
			response.setResult(result);
		} catch (Throwable e) {
			response.setError(e);
		}
		//写入outbundle(及SrpcEncoder)进行下一步处理（及编码）后发送到channel中给客户端
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		
	}

	private Object handler(SRPCRequest request) throws Throwable {
		String className = request.getClassName();
		
		//拿到实现类
		Object serviceBean = handlerMap.get(className);
		//拿到要调用的方法
		String methodName = request.getMethodName();
		
		Class<?>[] parameterTypes = request.getParameterTypes(); 
		
		Object[] paramters = request.getParameters();
		
		//调用实现类对应方法并返回结果
		Class<?> forName = Class.forName(className);
		Method method = forName.getMethod(methodName, parameterTypes);
		
		return method.invoke(serviceBean, paramters);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LOGGER.error("server caught exception",cause);
		ctx.close();
	}
}
