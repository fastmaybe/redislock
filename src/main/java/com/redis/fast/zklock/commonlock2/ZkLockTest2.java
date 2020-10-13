package com.redis.fast.zklock.commonlock2;

/**
 * @Author: liulang
 * @Date: 2020/10/13 17:02
 */
@SuppressWarnings("all")
public class ZkLockTest2 {


    public static void main(String[] args) {

        for (int i = 0; i < 5; i++) {
            new Thread(()->{
                meth();
            }).start();
        }

    }

    public static void meth() {
        DistributeLock2 session = new DistributeLock2();
        session.lock();
            try {
                Thread.sleep(3000);
                System.err.println("===============: "+System.currentTimeMillis()/1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        session.unLock();
    }
}
