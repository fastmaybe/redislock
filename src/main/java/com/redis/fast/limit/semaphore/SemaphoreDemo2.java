package com.redis.fast.limit.semaphore;

import java.util.concurrent.Semaphore;

/**
 * @Author: liulang
 * @Date: 2020/10/19 17:41
 *
 * Semaphore 信号量
 * API
 * acquire() 获取许可证（1 个 可查看源码）
 * acquire(int n) 获取许可证（n 个 ）
 *availablePermits()  查询剩余可用 许可证数量
 *drainPermits()  acquire and return 拿走剩余许可证
 * release() 释放一个
 * release(n) 释放n个
 *
 *
 * https://segmentfault.com/a/1190000023468516
 */
@SuppressWarnings("all")
public class SemaphoreDemo2 {

    public static void main(String[] args) {
        //信号量 第一个参数  允许多少个并发  fair 是否是公平锁
        //公平锁 ： 按照来获取锁顺序  来获取锁
        Semaphore semaphore = new Semaphore(2, true);
        Thread aa = new Thread(new MyRunnable(1, semaphore), "AA");
        Thread bb = new Thread(new MyRunnable(2, semaphore), "BB");
        Thread cc = new Thread(new MyRunnable(1, semaphore), "CC");

        aa.start();
        bb.start();
        cc.start();


    }
}

class MyRunnable implements Runnable{

    private int n;
    private Semaphore semaphore;

    public MyRunnable(int i, Semaphore semaphore) {
        this.n = i;
        this.semaphore = semaphore;
    }


    /**
     * 坑1：
     * semaphore.drainPermits  拿走剩余全部的许可证 不能用在这里  否现 释放不掉 最后 会卡死
     *
     * 如果这是 公平锁  a线程 先拿到锁  然后拿走 1 个许可证 然后又拿走剩下的许可证（2-1）；但是释放时候 只释放了 1 个；
     *                              因此剩下的许可证只有1 一个 ；按序 b线程进来 b线程需要2个 拿不到2个 进入等待了；c线程也不回执行了
     *
     * 如果 非公平锁  只有b线程先进去 此刻 才能执行完 且是因为 设定的巧合 即 向量许可证初始化为 2 而 a c线程只要1个 b 2个  是因为巧合
     *
     * 坑2：release（） 方法：源码注释说： 释放许可证的  不一定  是获取了许可证的  SemaphoreError 展示
     *          假设 一个线程中断了 还是执行了release（） 这个时候导致结果是  线程没有获取锁 却释放了锁 表现结果是 剩余许可证+1 总体许可证+1
     */
    @Override
    public void run() {
        try {
            //申请n 个可入令牌
          semaphore.acquire(n);
            System.out.println("剩余可用许可证："+semaphore.drainPermits());
            System.out.println(Thread.currentThread().getName()+"业务执行完成....");

        } catch (InterruptedException e) {
            // 线程中断
            e.printStackTrace();
        } finally {
        semaphore.release(n);
        }
    }
}
