package com.redis.fast.queue.diy;

/**
 * @Author: liulang
 * @Date: 2020/9/4 16:46
 */

import java.util.Arrays;

/**
 *  闭环循环覆盖 初始化指定大小 后不可以更改
 * @param <E>
 */
public class
LoopCoverQueue<E> implements Queue<E> {


    /**
     * 容器
     */
    private E[] data;

    /**
     * 头
     */
    private int head;

    /**
     * 尾
     */
    private int tail;

    private int capacity;


    private int size;

    public LoopCoverQueue(int capacity){
        if (capacity < 1){
            throw new RuntimeException("capacity must more than 0");
        }
        this.capacity = capacity;
        data = (E[])new Object[capacity];
    }


    /**
     * 入队
     * @param e
     */
    @Override
    public void enqueue(E e) {
        boolean cover = false;
        if (!isEmpty() && head == tail){
          cover = true;
        }
        data[tail] = e;
        tail = (tail+1) % capacity;
        if (cover) head = tail;
        if (!cover) size++;
    }

    public E enqueueIfCover(E e){
        boolean cover = head == tail;
        E old = null;
        if (cover){
            old = data[head];
        }
        data[tail] = e;
        tail = (tail+1) % capacity;
        if (cover) head = tail;
        if (!cover) size--;
        return old;
    }

    /**
     * 出列
     * @return
     */
    @Override
    public E dequeue() {
        if (isEmpty()){
            return null;
        }
        E e = data[head];
        head = (head+1) % capacity;
        size--;
        return e;
    }

    /**
     * 获取 但是不出列
     * @return
     */
    @Override
    public E getFront() {
        if (isEmpty()){
            return null;
        }
        return data[head];
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public String toString() {
       return Arrays.toString(data);
    }

    public static void main(String[] args) {
        LoopCoverQueue<Integer> queue = new LoopCoverQueue<>(2);

        Integer a = queue.enqueueIfCover(1);
        System.out.println(queue);
        System.out.println(a);
        Integer b = queue.enqueueIfCover(2);
        System.out.println(queue);
        System.out.println(b);
        Integer c = queue.enqueueIfCover(3);

        System.out.println(queue);
        System.out.println(c);

    }
}
