package cc.sanddata.srpc.sample.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SRPCBootstrap {
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		new ClassPathXmlApplicationContext("spring.xml");
	}
}
