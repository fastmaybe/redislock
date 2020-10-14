package com.redis.fast.zklock.commonlock.lock3;

import com.redis.fast.zklock.commonlock.AbstractLockImpl;
import com.redis.fast.zklock.commonlock.Lock;

/**
 * @Author: liulang
 * @Date: 2020/10/13 22:34  [   ZookeeperReAbleDisLock ] test
 */
@SuppressWarnings("all")
public class LockTestDemo5 {


    public static void main(String[] args) {

        for (int i = 0; i < 5; i++) {
            new Thread(()->{
                methA();
            }).start();
        }

    }

    public static void methA() {
        AbstractLockImpl lock = new ReentrantZKLock();

            lock.lock();
            try {
                System.err.println(Thread.currentThread().getName()+"=====AAAAAAAAAA===  **********BEGIN    **********====: "+System.currentTimeMillis()/1000L);
                Thread.sleep(3000);
                System.err.println(Thread.currentThread().getName()+"=====AAAAAAAAAA=======: "+System.currentTimeMillis()/1000L);
                methB(lock);
            } catch (InterruptedException e) {
                e.printStackTrace();
                lock.close();
            }
            lock.unlock();

    }

    public static void methB(Lock lock){
        lock.lock();
        try {
            System.err.println(Thread.currentThread().getName()+"=======BBBBBBBBBb========: "+System.currentTimeMillis()/1000L);
            Thread.sleep(3000);
            System.err.println(Thread.currentThread().getName()+"=======BBBBBBBBBb====********* END ***********====: "+System.currentTimeMillis()/1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lock.unlock();
    }
}
