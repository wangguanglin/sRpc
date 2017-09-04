package cc.sanddata.srpc.sample.server;

import org.springframework.stereotype.Component;

import cc.sanddata.srpc.sample.client.Person;
import cc.sanddata.srpc.sample.client.SampleHelloworld;
import cc.sanddata.srpc.server.SRPCService;

@SRPCService(SampleHelloworld.class)
public class SampleHelloWorldImpl implements SampleHelloworld {

	@Override
	public String hello(String name) {
		System.out.println("已经调用服务端接口实现，业务处理结果为：");
    	System.out.println("Hello! " + name);
        return "Hello! " + name;
	}

	@Override
	public String hello(Person person) {
		System.out.println("已经调用服务端接口实现，业务处理为：");
    	System.out.println("Hello! " + person.getName());
        return "Hello! " + person.getName();
	}

}
