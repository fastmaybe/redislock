package com.redis.fast.zklock.commonlock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: liulang
 * @Date: 2020/10/13 15:38
 */
@SuppressWarnings("all")
public class DistributeLock extends AbstractLockImpl {

    private ZkClient zkClient = new ZkClient("127.0.0.1:2181");

    private final String PATH = "/path";

    //当前节点
    private String currentPath ;

    //当前节点的上一个节点
    private String beforePath ;

    private CountDownLatch latch = null;

    public DistributeLock(){
        if (!zkClient.exists(PATH)){
            zkClient.createPersistent(PATH);
        }
    }



    @Override
    public boolean tryLock() {
        //如果当前节点为空 标识 第一次尝试加锁
        if (currentPath == null || currentPath.length() <= 0) {
            //创建临时有序节点
            currentPath = zkClient.createEphemeralSequential(PATH+"/","");
        }
        //获取路径下全部子节点
        List<String> childrens = zkClient.getChildren(PATH);
        //排序
        Collections.sort(childrens);

        if (currentPath.equals(PATH+"/"+childrens.get(0))){
            //自己就是最小节点
            return true;
        }else {
            //不是最小节点  找出自己上一个节点
            int index = Collections.binarySearch(childrens, currentPath.replace(PATH + "/", ""));
            beforePath = PATH+"/"+childrens.get(index -1);
        }
        return false;
    }

    @Override
    public void waitLock() {
        IZkDataListener listener = new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {
                //do nothing
            }
            @Override
            public void handleDataDeleted(String s) throws Exception {
                if (latch != null) {
                    //放开阻塞
                    latch.countDown();
                }
            }
        };
        //监听上一个节点

        zkClient.subscribeDataChanges(beforePath, listener);

        if (zkClient.exists(beforePath)){
            //上个节点存在  加入计数器阻塞
            latch = new CountDownLatch(1);
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //最后 取消 监听
        zkClient.unsubscribeDataChanges(beforePath,listener);
    }


    @Override
    public void unlock() {
        zkClient.delete(currentPath);
        zkClient.close();
    }


}
