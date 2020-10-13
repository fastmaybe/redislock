package com.redis.fast.zklock.commonlock;

/**
 * @Author: liulang
 * @Date: 2020/10/13 18:30
 */
@SuppressWarnings("all")
public class LockTestDemo {

    public static void main(String[] args) {

        for (int i = 0; i < 5; i++) {
            new Thread(()->{
                meth();
            }).start();
        }

    }

    public static void meth() {
        Lock session = new DistributeLock();
        session.lock();
        try {
            Thread.sleep(5000);
            System.err.println("===============: "+System.currentTimeMillis()/1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        session.unlock();
    }
}
