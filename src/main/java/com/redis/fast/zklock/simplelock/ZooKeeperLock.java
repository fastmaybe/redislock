package com.redis.fast.zklock.simplelock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * ZooKeeperSession
 * @author Administrator
 *                                  基于异常机制获取锁 简单版本
 *
 */
public class ZooKeeperLock {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    private ZooKeeper zookeeper;
    private CountDownLatch latch;

    public ZooKeeperLock() {
        try {
            this.zookeeper = new ZooKeeper(
                    "127.0.0.1:2181",
                    50000,
                    new ZooKeeperWatcher());
            try {
                connectedSemaphore.await();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("ZooKeeper session established......");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取分布式锁
     * @param productId
     */
    public Boolean acquireDistributedLock(Long productId) {
        String path = "/product-lock-" + productId;

        try {
            zookeeper.create(path, "".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.err.println("............获取锁............");
            return true;
        } catch (Exception e) {
            while(true) {
                try {
                    Stat stat = zookeeper.exists(path, true); // 相当于是给node注册一个监听器，去看看这个监听器是否存在
                    if(stat != null) {
                        this.latch = new CountDownLatch(1);
                        this.latch.await();
                        this.latch = null;
                    }
                    zookeeper.create(path, "".getBytes(),
                            ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                    System.err.println("............获取锁............");
                    return true;
                } catch(Exception m) {
                    continue;
                }
            }

        }
    }

    /**
     * 释放掉一个分布式锁
     * @param productId
     */
    public void releaseDistributedLock(Long productId) {
        String path = "/product-lock-" + productId;
        try {
            zookeeper.delete(path, -1);
            System.err.println("............释放............");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 建立zk session的watcher
     * @author Administrator
     *
     */
    private class ZooKeeperWatcher implements Watcher {

        @Override
        public void process(WatchedEvent event) {
            System.out.println("Receive watched event: " + event.getState());

            if(Event.KeeperState.SyncConnected == event.getState()) {
                connectedSemaphore.countDown();
            }

            if(latch != null) {
                latch.countDown();
            }
        }

    }


}
