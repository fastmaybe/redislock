package com.redis.fast.limit.semaphore;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Semaphore;

/**
 * @Author: liulang
 * @Date: 2020/10/19 18:37
 *
 * 增强Semaphore  解决release 问题
 */
public class MySemaphore extends Semaphore {

    private final ConcurrentLinkedDeque<Thread> queue =
            new ConcurrentLinkedDeque<>();


    public MySemaphore(int permits) {
        super(permits);
    }

    public MySemaphore(int permits, boolean fair) {
        super(permits, fair);
    }

    @Override
    public boolean tryAcquire() {
      final   boolean b = super.tryAcquire();
      if (b){
          //线程成功获取 加入当前线程
          this.queue.add(Thread.currentThread());
      }
      return b;
    }


    @Override
    public void release() {
      final   Thread thread = Thread.currentThread();
      if (!this.queue.contains(thread)){
          return;
      }
        super.release();
      this.queue.remove(thread);
    }
}
