package cc.sanddata.srpc.sample.client;

import cc.sanddata.sample.app.Person;
import cc.sanddata.sample.app.SampleHelloworld;
import cc.sanddata.srpc.client.SRPCProxy;
import cc.sanddata.srpc.registry.ServiceDiscovery;

/**
 * Created by wangguanglin on 2019/7/24.
 */
public class SRPCClientBootStrap {

    public static void main(String[] args) {
        ServiceDiscovery serviceDiscovery = new ServiceDiscovery("localhost:2181");
        SRPCProxy srpcProxy = new SRPCProxy(serviceDiscovery);
        SampleHelloworld helloworld = srpcProxy.create(SampleHelloworld.class);
        System.out.println(helloworld.hello("Wang"));
        System.out.println(helloworld.hello(new Person("Wong")));
        for(int i=0;i<100;i++) {
            SampleHelloworld helloworld1 = srpcProxy.create(SampleHelloworld.class);
            System.out.println(helloworld1.hello("Wang"+i));
            System.out.println(helloworld1.hello(new Person("Wong"+i)));
        }
    }
}
