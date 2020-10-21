package com.redis.fast.limit.semaphore;

import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @Author: liulang
 * @Date: 2020/10/19 18:16
 * 错误示例    release的坑  是存在这个问题 的风险 没有获取锁 最后却执行了 释放锁
 */
@SuppressWarnings("all")
public class SemaphoreError {
    public static void main(String[] args) throws InterruptedException {

        Integer parkSpace = 3;
        System.out.println("这里有" + parkSpace + "个停车位,先到先得啊！");
        Semaphore semaphore = new Semaphore(parkSpace, true);

        Thread threadA = new Thread(new ParkCar(1, "布加迪", semaphore), "赵四");
        Thread threadB = new Thread(new ParkCar(2, "法拉利", semaphore), "刘能、谢广坤");
        Thread threadC = new Thread(new ParkCar(1, "劳斯莱斯", semaphore), "why哥");

        threadA.start();
        threadC.start();
        threadB.start();

        //模拟线程b 不等了 中断线程走人
        threadB.interrupt();

        /**
         * 因此 需要再捕获异常 里面直接 return 不让其执行finnaly
         * 此刻有个疑问  上述情况是  没有获取锁 线程中断
         *
         * 如果是 线程刚刚获取了锁  但是线程中断呢  源码里面有介绍 对acquire（int n）
         * 抛出 InterruptedException 后，分配给这个线程的所有许可证都会被分配给其他想要获取许可证的线程，就像通过调用 release 方法一样。
         */
    }
}

@SuppressWarnings("all")
class ParkCar implements Runnable {

    private int n;
    private String carName;
    private Semaphore semaphore;

    public ParkCar(int n, String carName, Semaphore semaphore) {
        this.n = n;
        this.carName = carName;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        try {
            if (semaphore.availablePermits() < n) {
                System.err.println(Thread.currentThread().getName() + "来停车,但是停车位不够了,等着吧");
            }
            semaphore.acquire(n);
            System.out.println(Thread.currentThread().getName() + "把自己的" + carName + "停进来了,剩余停车位:" + semaphore.availablePermits() + "辆");
            //模拟停车时长

            int parkTime = ThreadLocalRandom.current().nextInt(1, 6);
            TimeUnit.SECONDS.sleep(parkTime);
            System.out.println(Thread.currentThread().getName() + "把自己的" + carName + "开走了,停了" + parkTime + "小时");
        } catch (Exception e) {
//            e.printStackTrace();
            //TODO 此处应该加入 return
            System.err.println(Thread.currentThread().getName()+"不等了走人");
        } finally {
            semaphore.release(n);
            System.out.println(Thread.currentThread().getName() + "走后,剩余停车位:" + semaphore.availablePermits() + "辆");
        }
    }
}
