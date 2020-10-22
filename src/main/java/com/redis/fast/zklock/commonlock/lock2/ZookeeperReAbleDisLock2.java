package com.redis.fast.zklock.commonlock.lock2;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: liulang
 * @Date: 2020/10/13 23:13
 */
public class ZookeeperReAbleDisLock2 {


    private ZkClient client;

    private String lockPath;

    private String currentPath;

    private String beforePath;

    // 线程获取锁的次数
    private static volatile int state;
    // 当前获取锁的线程
    private static volatile Thread thread;

    public ZookeeperReAbleDisLock2(String lockPath) {
        super();
        this.lockPath = lockPath;

        client = new ZkClient("127.0.0.1:2181");
//        client.setZkSerializer(new MyZkSerializer());

        if (!client.exists(lockPath)) {
            try {
                client.createPersistent(lockPath);
            } catch (Exception e) {
            }
        }

    }

    public boolean tryLock() {
        return tryLock(1);
    }

    public boolean tryLock(int acquires) {
        // 获取当前线程
        final Thread currntThread = Thread.currentThread();
        // 获取当前锁的次数
        int state = getState();

        // state == 0 表示没有线程获取锁 进来的线程肯定能获取锁
        if (state == 0) {
            if (compareAndSetState(0, acquires, currntThread)) {
                return true;
            }

            // state != 0 表示线程被获取了 判断是否是当前线程 如果是则 state+1 ,否则返回false
        } else if (currntThread == getThread()) {
            int nextS = getState() + acquires;

            if (nextS < 0) {
                throw new Error("Maximum lock count exceeded");
            }
            // 这里不需要cas 原因不解释
            setState(nextS);
            System.out.println(Thread.currentThread().getName() + ":获取重入锁成功,当前获取锁次数: " + getState());
            return true;
        }

        // 获取锁的不是当前线程
        return false;
    }

    public boolean compareAndSetState(int expect, int update, Thread t) {

        if (this.currentPath == null) {
            currentPath = this.client.createEphemeralSequential(lockPath + "/", "1");
            System.err.println(Thread.currentThread().getName() + "尝试获取锁；当前节点" + currentPath);
        }else {
            System.err.println(Thread.currentThread().getName() + "尝试获取锁；当前节点 (已经存在)" + currentPath);
        }
        // 获取所有节点
        List<String> children = this.client.getChildren(lockPath);

        // 排序
        Collections.sort(children);

        // 判断当前节点是否为最小节点
        if (getState() == expect && currentPath.equals(lockPath + "/" + children.get(0))) {
            setState(update);
            thread = t;
            System.out.println(Thread.currentThread().getName() + ":获取锁成功,当前获取锁次数: " + getState());
            return true;
        }

        // 取得前一个
        // 得到字节的索引号
        int curIndex = children.indexOf(currentPath.substring(lockPath.length() + 1));
        beforePath = lockPath + "/" + children.get(curIndex - 1);

        return false;
    }

    public final boolean tryRelease(int releases) {
        // 可以判断是否自己获得锁 自己获得锁才能删除
        final Thread currentThread = Thread.currentThread();
        // 获取锁的不是当前线程
        if (currentThread != getThread()) {
            // throw new IllegalMonitorStateException();
            return false;
        }

        // 释放锁的次数
        int nextS = getState() - releases;
        boolean free = false;
        if (nextS == 0) {
            free = true;
            setThread(null);
            // 删除zk节点
            client.delete(currentPath);
            System.out.println(Thread.currentThread().getName() + ": 所有锁释放成功:删除zk节点...");
        }

        setState(nextS);

        if (!free) {
            System.out.println(Thread.currentThread().getName() + ": 释放重入锁成功: 剩余锁次数：" + getState());
        }

        return free;
    }

    public void lock() {
        if (!tryLock()) {
            // 没有获得锁，阻塞自己
            waitForLock();
            // 再次尝试加锁
            lock();
        }
    }

    private void waitForLock() {
        CountDownLatch cdl = new CountDownLatch(1);

        IZkDataListener listener = new IZkDataListener() {

            @Override
            public void handleDataChange(String arg0, Object arg1) throws Exception {

            }

            @Override
            public void handleDataDeleted(String arg0) throws Exception {
                System.out.println("节点被删除了，开始抢锁...");
                cdl.countDown();
            }

        };
        // 完成watcher注册
        this.client.subscribeDataChanges(beforePath, listener);

        // 阻塞自己
        if (this.client.exists(beforePath)) {
            try {
                cdl.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 取消注册
        this.client.unsubscribeDataChanges(beforePath, listener);
    }

    public void unlock() {
        // 可以判断是否自己获得锁 自己获得锁才能删除
        // client.delete(currentPath);
        tryRelease(1);

    }

    public static int getState() {
        return state;
    }

    public static void setState(int state) {
        ZookeeperReAbleDisLock2.state = state;
    }

    public static Thread getThread() {
        return thread;
    }

    public static void setThread(Thread thread) {
        ZookeeperReAbleDisLock2.thread = thread;
    }


}
