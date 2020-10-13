package com.redis.fast.zklock.commonlock;

/**
 * @Author: liulang
 * @Date: 2020/10/13 15:38
 */
public class DistributeLock extends AbstractLockImpl {



    @Override
    public void unlock() {


    }



    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public void waitLock() {

    }
}
