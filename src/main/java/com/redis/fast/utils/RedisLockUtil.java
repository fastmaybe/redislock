package com.redis.fast.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @Author: liulang
 * @Date: 2020/10/12 15:18
 */
@Component
public class RedisLockUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    private final String   releaseScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";


    private final String   expandScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('expire', KEYS[1],ARGV[2]) else return 0 end";


    public Boolean lock(String key , String value, Long expire , TimeUnit unit){
        return redisTemplate.opsForValue().setIfAbsent(key,value,expire,unit);
    }

    public Boolean releaseLock(String key , String value ){
        DefaultRedisScript<Long> script = new DefaultRedisScript<Long>(releaseScript);
        script.setResultType(Long.class);
        Long execute = (Long) redisTemplate.execute(script,Collections.singletonList(key), value);
        return  1L == execute;
    }

    public Boolean expandLockTime(String key , String value,Long lockTime){
        DefaultRedisScript<Long> script = new DefaultRedisScript<Long>(expandScript,Long.class);
        Long execute = (Long) redisTemplate.execute(script, Collections.singletonList(key), value, lockTime);
        return  1L == execute;
    }
}
