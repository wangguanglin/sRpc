package cc.sanddata.srpc.sample.client;

public interface SampleHelloworld {
	String hello(String name);
	
	String hello(Person person);
}
