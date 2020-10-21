package com.redis.fast.limit.semaphore;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

/**
 * @Author: liulang
 * @Date: 2020/10/19 16:23
 */
@SuppressWarnings("all")
public class SemaphoreDemo {
    static  Semaphore permit = new Semaphore(10, true);
    static CountDownLatch latch = new CountDownLatch(20);

    public static void main(String[] args) throws InterruptedException {

        long begin = System.currentTimeMillis();
        for (int i = 0; i < 20; i++) {
            int finalI = i;
            new Thread(()->{
                process(finalI);
            }).start();
        }
        latch.await();
        long end = System.currentTimeMillis();
        System.err.println(end-begin);

    }




    public static void process(int i){

        try{
            permit.acquire();
            System.out.println(i);
            Thread.sleep(2000);
            //业务逻辑处理
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            latch.countDown();
            permit.release();
        }
    }

}
