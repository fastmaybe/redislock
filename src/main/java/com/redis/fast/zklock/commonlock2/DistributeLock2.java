package com.redis.fast.zklock.commonlock2;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * DEMO 1 简单版  有序节点 分布式锁
 */

/**
 * @Author: liulang
 * @Date: 2020/10/13 15:45
 */
public class DistributeLock2  {

    //先生成一个 持久话的节点parentPath
    private String parentPath = "/distributeLock2";
    private String prePath = parentPath+"/";
    private String lockPath ;

    private ZooKeeper zooKeeper;

    private CountDownLatch downLatch = new CountDownLatch(1);
    private CountDownLatch wait ;

    private String beforePath;





    public DistributeLock2()  {
        try {
            zooKeeper = new ZooKeeper("127.0.0.1:2181", 3000, event -> {
                if (event.getState() == Watcher.Event.KeeperState.SyncConnected){
                    downLatch.countDown();
                    if (event.getType() == Watcher.Event.EventType.NodeDeleted){
                        //监听节点 删除事件
                        //被删除的节点  应该是 我注册监听的节点
                        if (event.getPath().equals(beforePath) && wait != null){
                            wait.countDown();
                        }
                    }
                }
            });
            downLatch.await();
            Stat stat = zooKeeper.exists(parentPath, false);
            if (stat == null){
                System.err.println("init  parent path");
                try {
                    zooKeeper.create(parentPath,"".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
                    //或者提前生成好  持久话节点
                } catch (KeeperException e) {
                } catch (InterruptedException e) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }





    public void lock()  {
        try {
             lockPath = zooKeeper.create(prePath+"commonlock2", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

            List<String> children = zooKeeper.getChildren(parentPath, false);
            //升序排列
            Collections.sort(children);

            if (lockPath.equals(prePath + children.get(0))){

//                return  true;
            }else {
                //不等于 获取锁失败 找到上一个节点
                //二分查找 集合是有序的
                int index = Collections.binarySearch(children, lockPath.replace(prePath, ""));
                 beforePath = prePath + children.get(index - 1);

                 //然后 注册监听 监听上一个节点
                Stat stat = zooKeeper.exists(beforePath, true);
                if (stat == null){

//                    return true;
                }
                else {

                    wait = new CountDownLatch(1);
                    wait.await();
//                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
//            return false;
        }
    }

    public void unLock()  {

        try {
            zooKeeper.delete(lockPath,-1);
            zooKeeper.close();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }


}
