package com.redis.fast.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application.properties")
public class SailTicketTest {


    @Resource
    private JedisPool jedisPool;

    @Test
    public void getJe(){
        Jedis resource = jedisPool.getResource();
        resource.set("1","2");
    }


}
