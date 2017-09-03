package cc.sanddata.srpc.registry;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceRegistry {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);
	
	private CountDownLatch latch = new CountDownLatch(1);
	
	private String registryAddress;
	
	public ServiceRegistry(String registryAddress){
		this.registryAddress = registryAddress;
	}
	
	public void registry(String data){
		if (data!=null){
			ZooKeeper zk = connectServer();
			if(zk != null ){
				createNode(zk,data);
			}
		}
	}

	private void createNode(ZooKeeper zk, String data) {
		try {
			byte[] bytes = data.getBytes();
			if (zk.exists(Constand.ZK_REGISTRY_PATH, null) == null){
				zk.create(Constand.ZK_REGISTRY_PATH, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
//			if (zk.exists(Constand.ZK_DATA_PATH, null) == null){
//				zk.create(Constand.ZK_DATA_PATH, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
//			}
//			String serverPath = Constand.ZK_DATA_PATH+"/server-";
			String path = zk.create(Constand.ZK_DATA_PATH, bytes, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
			
			LOGGER.debug("create zookeeper node (() => {})",path,data);
		} catch (Exception e) {
			LOGGER.error("",e);
		}
		
	}

	private ZooKeeper connectServer() {
		ZooKeeper zk = null;
		try {
			zk = new ZooKeeper(registryAddress, Constand.ZK_SESSION_TIMEOUT, new Watcher() {
				
				@Override
				public void process(WatchedEvent event) {
					if (event.getState() == Event.KeeperState.SyncConnected){
						latch.countDown();
					}
					
				}
			});
			latch.await();
		} catch (Exception e) {
			LOGGER.error("",e);
		}
		return zk;
	}
}
