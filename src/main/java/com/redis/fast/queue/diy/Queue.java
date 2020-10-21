package com.redis.fast.queue.diy;

/**
 * @Author: liulang
 * @Date: 2020/9/4 15:24
 */
public interface Queue<E> {

    void enqueue(E e);


    E dequeue();

    E getFront();

    int getSize();

    boolean isEmpty();
}
