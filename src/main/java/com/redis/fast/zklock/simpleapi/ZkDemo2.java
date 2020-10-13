package com.redis.fast.zklock.simpleapi;

import org.apache.zookeeper.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: liulang
 * @Date: 2020/10/13 16:09
 */
public class ZkDemo2 implements Watcher {

    static ZooKeeper zooKeeper;
    static CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
        zooKeeper = new ZooKeeper("127.0.0.1:2181", 4000, new ZkDemo2());
        latch.await();
        String result = zooKeeper.create("/abcde",
                "node10".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL);

        String result2 = zooKeeper.create("/abcde",
                "node10".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL);

        System.out.println(result);
        System.out.println(result2);



        Thread.sleep(800000);

    }

    @Override
    public void process(WatchedEvent event) {

        if (event.getState() == Event.KeeperState.SyncConnected){
            System.out.println(event.getPath());
            System.out.println(event.getType());
            System.out.println("连接成功了================");
            latch.countDown();
        }


    }
}
