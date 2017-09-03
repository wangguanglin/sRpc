package cc.sanddata.srpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

import cc.sanddata.srpc.comm.SRPCRequest;
import cc.sanddata.srpc.comm.SRPCResponse;
import cc.sanddata.srpc.registry.ServiceDiscovery;

/**
 * 
 * @ClassName: SRPCProxy 
 * @Description: 用于创建SRPC服务代理
 * @author wanggl
 * @date 2017年8月31日 下午8:55:09 
 *
 */
public class SRPCProxy {
	
	private String serverAddress;
	
	private ServiceDiscovery serviceDiscovery;

	public SRPCProxy(String serverAddress) {
		super();
		this.serverAddress = serverAddress;
	}

	public SRPCProxy(ServiceDiscovery serviceDiscovery) {
		super();
		this.serviceDiscovery = serviceDiscovery;
	}
	/**
	 * 
	 * @Title: create 
	 * @author wanggl
	 * @Description: 创建代理
	 * @param @param interfaceClass
	 * @param @return 
	 * @return T
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public <T> T create(Class<?> interfaceClass){
		return (T)Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler() {
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				//创建SRPCRequest，封装被代理的属性
				SRPCRequest request = new SRPCRequest();
				request.setRequestId(UUID.randomUUID().toString());
				//拿到声明这个方法的业务接口名称
				request.setClassName(method.getDeclaringClass().getName());
				request.setMethodName(method.getName());
				request.setParameterTypes(method.getParameterTypes());
				request.setParameters(args);
				//查找服务
				if (serviceDiscovery !=null) {
					serverAddress = serviceDiscovery.discover();
				}
				
				//随机获取服务的地址
				String[] array = serverAddress.split(":");
				String host = array[0];
				int port = Integer.parseInt(array[1]);
				//创建Netty实现的RpcClient，链接服务端
				SRPCClient client = new SRPCClient(host, port);
				//通过netty向服务端发送请求
				SRPCResponse response = client.send(request);
				//返回信息
				if (response.isError()) {
					throw response.getError();
				} else {
					return response.getResult();
				}
			}
		});
	}
}
