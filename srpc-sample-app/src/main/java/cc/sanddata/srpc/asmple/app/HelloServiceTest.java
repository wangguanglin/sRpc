package cc.sanddata.srpc.asmple.app;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cc.sanddata.srpc.client.SRPCProxy;
import cc.sanddata.srpc.sample.client.Person;
import cc.sanddata.srpc.sample.client.SampleHelloworld;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring.xml")
public class HelloServiceTest {
	
	@Autowired
	SRPCProxy proxy;
	
	@Test
	public void test1() {
		SampleHelloworld helloworld = proxy.create(SampleHelloworld.class);
		
		String result = helloworld.hello("World");
		System.out.println("服务端返回结果：");
		System.out.println(result);

	}
	
	@Test
	public void helloTest2() {
		SampleHelloworld helloService = proxy.create(SampleHelloworld.class);
		String result = helloService.hello(new Person("Wang"));
		System.out.println("服务端返回结果：");
		System.out.println(result);
	}
}
