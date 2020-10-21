package com.redis.fast.queue.diy;

import java.util.Arrays;

/**
 * @Author: liulang
 * @Date: 2020/9/4 15:27
 * 循环队列   不可以覆盖的情况
 */
public class LoopQueue<E> implements  Queue<E> {

    private E[] data;//容器

    private int front;//队首

    private int tail; //队尾

    private int size; //队列元素的个数

    private int level = 2; //扩容因子

    /**
     * 指定容量 初始化队列大小 不可以覆盖的情况 考虑浪费一个空间 区分队列满了 和队列没有满的情况
     * @param capacity
     */
    public LoopQueue(int capacity){
        data = (E[])new Object[capacity+1];
    }

    public LoopQueue(){
        this(10);
    }




    @Override
    public void enqueue(E e) {
        //检查队列是否已满
        if ((tail+1) == front){
            //队列已满 扩容
            System.err.print("...扩容...");
            resize(getCapacity() * 2);
        }
        data[tail] = e;
        tail = (tail+1) % data.length;
        size++;
    }

    @Override
    public E dequeue() {
        if (isEmpty()){
            throw new IllegalArgumentException("队列为空");
        }
       E e =  data[front];
        //元素出队
        data[front] = null;
        //维护front的位置
        front = (front+1) % data.length;
        //维护size大小
        size--;

        //元素出队后 可以判定条件缩容
        if (size == getCapacity() / level && getCapacity() / level != 0) {
            System.err.print("...缩容...");
            resize(getCapacity() / 2);
        }
        return e;
    }



    @Override
    public E getFront() {
        if (isEmpty()){
            return null;
        }
        return data[front];
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return front == tail;
    }

    private int getCapacity(){
        //初始化时候  设置了一个容量浪费
        return data.length - 1 ;
    }

    private void resize(int newCapacity) {

        //设置新的容器大小
        E[] newData = (E[]) new Object[newCapacity + 1];

        for (int i = 0; i < size; i++) {

            //新容器的首位 再旧容器有一个 front 的偏移量
            newData[i] = data[(i + front) % data.length];
        }

        data = newData;
        front = 0;
        tail = size;
    }

    @Override
    public String toString() {
        return "LoopQueue{" +

                "【队首】data=" + Arrays.toString(data) + "【队尾】" +

                ", front=" + front +

                ", tail=" + tail +

                ", size=" + size +

                ", capacity=" + getCapacity() +

                '}';
    }




    public static void main(String[] args) {


        LoopQueue<Integer> queue = new LoopQueue<>();


        for (int i = 0; i < 10; i++) {
            queue.enqueue(i);
        }

        System.out.println(queue);


        System.out.println( queue.dequeue()+"出列");
        System.out.println(queue);
        System.out.println( queue.dequeue()+"出列");
        System.out.println(queue);
        System.out.println( queue.dequeue()+"出列");
        System.out.println(queue);


        System.out.print( "100 入列后：-----");
        queue.enqueue(100);
        System.out.println(queue);

        System.out.print( "101 入列后：-----");
        queue.enqueue(101);
        System.out.println(queue);

        System.out.print( "102 入列后：-----");
        queue.enqueue(102);
        System.out.println(queue);

        System.out.print( "103 入列后：-----");
        queue.enqueue(103);
        System.out.println(queue);

        System.out.print( "104 入列后：-----");
        queue.enqueue(104);
        System.out.println(queue);

        queue.dequeue();
        System.out.println(queue);
        queue.dequeue();
        System.out.println(queue);
        queue.dequeue();
        System.out.println(queue);
    }
}
