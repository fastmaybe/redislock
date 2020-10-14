package com.redis.fast.zklock.commonlock.lock2;

import com.redis.fast.zklock.commonlock.lock1.ZookeeperReAbleDisLock;



/**
 * @Author: liulang
 * @Date: 2020/10/13 23:17
 */
@SuppressWarnings("all")
public class LockTestDemo6 {

    public static void main(String[] args) {

        for (int i = 0; i < 5; i++) {
            new Thread(()->{
                methA();
            }).start();
        }

    }

    public static void methA() {
        ZookeeperReAbleDisLock2 lock = new ZookeeperReAbleDisLock2("/lockpath");
        lock.lock();
        try {
            System.err.println(Thread.currentThread().getName()+"=====AAAAAAAAAA=======: "+System.currentTimeMillis()/1000L);
            Thread.sleep(3000);
            methB(lock);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lock.unlock();
    }

    public static void methB(ZookeeperReAbleDisLock2 lock){
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
