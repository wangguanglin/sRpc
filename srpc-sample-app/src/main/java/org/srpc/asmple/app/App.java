package org.srpc.asmple.app;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cc.sanddata.srpc.client.SRPCProxy;
import cc.sanddata.srpc.sample.client.SampleHelloworld;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        ApplicationContext ctx =  new ClassPathXmlApplicationContext("spring.xml");
        SRPCProxy proxy = (SRPCProxy) ctx.getBean("rpcProxy");
        
        SampleHelloworld helloworld = proxy.create(SampleHelloworld.class);
		
		String result = helloworld.hello("World");
		System.out.println("服务端返回结果：");
		System.out.println(result);
    }
}
