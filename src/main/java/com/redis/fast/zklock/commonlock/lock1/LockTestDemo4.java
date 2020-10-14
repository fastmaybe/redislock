package com.redis.fast.zklock.commonlock.lock1;

import java.util.concurrent.locks.Lock;

/**
 * @Author: liulang
 * @Date: 2020/10/13 20:35  【 ZookeeperReAbleDisLock  】 test
  */
@SuppressWarnings("all")
public class LockTestDemo4 {

//    private static Lock lock = new ZookeeperReAbleDisLock("/lockpath");
    public static void main(String[] args) {

        for (int i = 0; i < 5; i++) {
            new Thread(()->{
                methA();
            }).start();
        }

    }

    public static void methA() {
        Lock lock = new ZookeeperReAbleDisLock("/lockpath");
        lock.lock();
        try {
            Thread.sleep(3000);
            System.err.println(Thread.currentThread().getName()+"=====AAAAAAAAAA=======: "+System.currentTimeMillis()/1000L);
            methB(lock);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lock.unlock();
    }

    public static void methB(Lock lock){
        lock.lock();
        try {
            Thread.sleep(3000);
            System.err.println(Thread.currentThread().getName()+"=======BBBBBBBBBb========: "+System.currentTimeMillis()/1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lock.unlock();
    }
}
