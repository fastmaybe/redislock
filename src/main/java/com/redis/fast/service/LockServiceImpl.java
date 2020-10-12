package com.redis.fast.service;

import com.redis.fast.aspect.LockAnnotation;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @Author: liulang
 * @Date: 2020/10/12 17:00
 */
@Service
public class LockServiceImpl {

    @Async
    @LockAnnotation(watchDog = true,waitTime = 100L)
    public void asyncMe() throws InterruptedException {
        Thread.sleep(10000L);
        System.out.println("完成");
        System.out.println();
    }
}
