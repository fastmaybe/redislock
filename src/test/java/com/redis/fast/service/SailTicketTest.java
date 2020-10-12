package com.redis.fast.service;

import com.redis.fast.pojo.User;
import com.redis.fast.utils.RedisLockUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application.properties")
public class SailTicketTest {


    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedisLockUtil redisLockUtil;

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Test
    public void testredis(){
        redisTemplate.opsForValue().set("1",new User("name",2));
//        Object o = redisTemplate.opsForValue().get("1");
//        System.out.println(o);
    }

    @Test
    public void testredis2(){
        stringRedisTemplate.opsForValue().set("1","2");
    }

    @Test
    public void testLock(){
        Boolean aBoolean = redisLockUtil.lock("2", "2",150L, TimeUnit.SECONDS);
    }
    @Test
    public void testRelease(){
        Boolean aBoolean = redisLockUtil.releaseLock("2", "3");
        System.out.println(aBoolean);
    }

    @Test
    public void expandLockTime(){
        Boolean aBoolean = redisLockUtil.expandLockTime("2", "2",250L);
        System.out.println(aBoolean);
    }
}
