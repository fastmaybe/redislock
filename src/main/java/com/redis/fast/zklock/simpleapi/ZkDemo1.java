package com.redis.fast.zklock.simpleapi;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

import static org.apache.zookeeper.Watcher.Event.EventType;
import static org.apache.zookeeper.Watcher.Event.KeeperState;

/**
 * @Author: liulang
 * @Date: 2020/10/13 13:43
 */
public class ZkDemo1 implements Watcher {

   static ZooKeeper zooKeeper;
    static CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
         zooKeeper = new ZooKeeper("127.0.0.1:2181", 4000, new ZkDemo1());
        latch.await();
//        String s = zooKeeper.create("/demo1", "demo1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        byte[] data = zooKeeper.getData("/demo1", true, null);
        System.out.println(new String(data));

        Thread.sleep(80000);

    }

    @Override
    public void process(WatchedEvent event) {

        System.out.println("触发watch。。。。。。。。。。。。。。。。");
        if (event.getState() == KeeperState.SyncConnected){
            System.out.println(event.getPath());
            System.out.println(event.getType());
            System.out.println("连接成功了================");
            latch.countDown();

            if (event.getType() == EventType.NodeDataChanged){
                try {
                    zooKeeper.getData(event.getPath(),this,null);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }


    }
}
