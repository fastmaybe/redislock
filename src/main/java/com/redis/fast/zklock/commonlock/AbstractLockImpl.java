package com.redis.fast.zklock.commonlock;

/**
 * @Author: liulang
 * @Date: 2020/10/13 15:09
 */
public abstract class AbstractLockImpl implements Lock{

    @Override
    public void lock() {

    }


    /**
     * 尝试拿锁
     * @return
     */
    public abstract boolean tryLock();

    /**
     * 等待锁
     */
    public abstract void waitLock();

}
