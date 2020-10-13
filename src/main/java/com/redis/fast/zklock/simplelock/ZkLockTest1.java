package com.redis.fast.zklock.simplelock;

/**
 * @Author: liulang
 * @Date: 2020/10/13 14:42
 */
@SuppressWarnings("all")
public class ZkLockTest1 {


    public static void main(String[] args) {

        for (int i = 0; i < 5; i++) {
            new Thread(()->{
                meth();
            }).start();
        }

    }

    public static void meth(){
        ZooKeeperLock session = new ZooKeeperLock();
        Boolean aBoolean = session.acquireDistributedLock(1L);
        if (aBoolean){
            try {
                Thread.sleep(3000);
                System.err.println("===============: "+System.currentTimeMillis()/1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        session.releaseDistributedLock(1L);
    }
}
