package com.redis.fast.utils;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

@Component
public class RedisLock implements Lock {

    private static final String KEY="KEY";

    @Resource
    private JedisPool jedisPool;

    public void lock() {
       if (tryLock()){
           return;
       }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lock();
    }

    public void lockInterruptibly() throws InterruptedException {

    }

    public boolean tryLock() {
        String uuid = UUID.randomUUID().toString();
        Jedis jedis = jedisPool.getResource();
        String set = jedis.set(KEY, uuid, "NX", "PX", 1000);
        if ("OK".equals(set)){
            return true;
        }
        return false;
    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    public void unlock() {


    }

    public Condition newCondition() {
        return null;
    }


}
