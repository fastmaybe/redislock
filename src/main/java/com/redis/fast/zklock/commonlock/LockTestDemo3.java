package com.redis.fast.zklock.commonlock;

/**
 * @Author: liulang
 * @Date: 2020/10/13 19:44
 */

/**
 * 经过测试   【   DistributeLock 是不可重入锁  运行下面 会死锁 】
 */
@SuppressWarnings("all")
public class LockTestDemo3 {

    public static void main(String[] args) {

        for (int i = 0; i < 5; i++) {
            new Thread(()->{
                methA();
            }).start();
        }

    }

    public static void methA() {
       Lock lock = new DistributeLock();
        lock.lock();
        try {
            Thread.sleep(3000);
            System.err.println(Thread.currentThread().getName()+"=====AAAAAAAAAA=======: "+System.currentTimeMillis()/1000L);
            methB();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lock.unlock();
    }

    public static void methB(){
        Lock lock = new DistributeLock();
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
