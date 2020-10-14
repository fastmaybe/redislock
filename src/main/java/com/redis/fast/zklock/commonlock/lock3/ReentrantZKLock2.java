package com.redis.fast.zklock.commonlock.lock3;

import com.redis.fast.zklock.commonlock.AbstractLockImpl;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: liulang
 * @Date: 2020/10/13 19:47
 */
public class ReentrantZKLock2 extends AbstractLockImpl {

    private ZkClient zkClient = new ZkClient("127.0.0.1:2181");

    private final String PATH = "/rentpath2";

    //当前节点
    private ThreadLocal<String> currentPath = new ThreadLocal<>();
//    private String currentPath ;

    //当前节点的上一个节点
//    private String beforePath ;
    private ThreadLocal<String> beforePath = new ThreadLocal<>();

    private static volatile Integer count;

    private volatile Thread currentThread;

//    private CountDownLatch latch = null;

    public ReentrantZKLock2 (){
        if (!zkClient.exists(PATH)){
            zkClient.createPersistent(PATH);
        }
    }



    @Override
    public boolean tryLock() {
        return tryLock(1);
    }

    private Boolean tryLock(int acquires){
        //获取当前线程
        Thread thread = Thread.currentThread();

        if (count == null || count ==0 ){
            //此时第一次获取锁

            if (currentPath.get() == null || currentPath.get().length() <= 0) {
                //创建临时有序节点
                String nodeName = zkClient.createEphemeralSequential(PATH+"/","");
                currentPath.set(nodeName);
            }

            //获取路径下全部子节点
            List<String> childrens = zkClient.getChildren(PATH);
            //排序
            Collections.sort(childrens);

            if (currentPath.get().equals(PATH+"/"+childrens.get(0))){
                //自己就是最小节点
                count = acquires;
                currentThread = thread;
                System.err.println(Thread.currentThread().getName()+" 获取锁次数"+count);
                return true;
            }else {
                //不是最小节点  找出自己上一个节点

                int index = Collections.binarySearch(childrens, currentPath.get().replace(PATH + "/", ""));
                beforePath.set(PATH+"/"+childrens.get(index -1));
//                System.err.println(beforePath.get());
                return false;
            }

        }else if (currentThread == thread){
            int nextS = count + acquires;
            if (nextS < 0){
                throw new Error("Maximum lock count exceeded");
            }
            count = nextS;
            System.err.println(Thread.currentThread().getName()+" 获取锁次数"+count);
            return true;
        }

        return false;
    }

    @Override
    public void waitLock() {

        CountDownLatch latch = new CountDownLatch(1);

        IZkDataListener listener = new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {
                //do nothing
            }
            @Override
            public void handleDataDeleted(String s) throws Exception {
                //放开阻塞
//                System.err.println(Thread.currentThread().getName()+"===放开阻塞====；节点被删除："+s);
                latch.countDown();

            }
        };
        //监听上一个节点

        zkClient.subscribeDataChanges(beforePath.get(), listener);

        if (zkClient.exists(beforePath.get())){
//            System.err.println(Thread.currentThread().getName()+"=== 阻塞====；监听"+beforePath.get());

            try {
                latch.await();
//                System.err.println(Thread.currentThread().getName()+"=== 继续运行====；监听"+beforePath.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //最后 取消 监听
        zkClient.unsubscribeDataChanges(beforePath.get(),listener);
        beforePath.remove();
    }


    @Override
    public void unlock() {
        Thread thread = Thread.currentThread();
        if (currentThread != thread){
            //非当前获取锁的线程 直接返回
            System.err.println("非当前线程");
            return ;
        }

// 释放锁的次数
        int nextS = count - 1;
        boolean free = false;
        if (nextS == 0) {
            free = true;
            currentThread = null;
            // 删除zk节点
            zkClient.delete(currentPath.get());
            currentPath.remove();
//            zkClient.close();
            System.err.println(Thread.currentThread().getName() + ": 所有锁释放成功 :删除zk节点..."+currentPath);
        }

        count = nextS;

        if (!free){
            System.err.println(Thread.currentThread().getName() + ": 释放重入锁成功: 剩余锁次数：" + count);
        }


    }
}
