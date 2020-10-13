package com.redis.fast.zklock.commonlock;

/**
 * @Author: liulang
 * @Date: 2020/10/13 15:08
 */
public interface Lock {

    void lock();

    void unlock();

}
