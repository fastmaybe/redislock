package com.redis.fast.redis_API.string;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * @Author: liulang
 * @Date: 2020/10/21 14:24
 *
 * String 原生API
 *
 *
 *  APPEND key value
 *   summary: Append a value to a key
 *   since: 2.0.0
 *
 *   BITCOUNT key [start end]
 *   summary: Count set bits in a string
 *   since: 2.6.0
 *
 *   BITFIELD key [GET type offset] [SET type offset value] [INCRBY type offset increment] [OVERFLOW WRAP|SAT|FAIL]
 *   summary: Perform arbitrary bitfield integer operations on strings
 *   since: 3.2.0
 *
 *   BITOP operation destkey key [key ...]
 *   summary: Perform bitwise operations between strings
 *   since: 2.6.0
 *
 *   BITPOS key bit [start] [end]
 *   summary: Find first bit set or clear in a string
 *   since: 2.8.7
 *
 *   DECR key
 *   summary: Decrement the integer value of a key by one
 *   since: 1.0.0
 *
 *   DECRBY key decrement
 *   summary: Decrement the integer value of a key by the given number
 *   since: 1.0.0
 *
 *   GET key
 *   summary: Get the value of a key
 *   since: 1.0.0
 *
 *   GETBIT key offset
 *   summary: Returns the bit value at offset in the string value stored at key
 *   since: 2.2.0
 *
 *   GETRANGE key start end
 *   summary: Get a substring of the string stored at a key
 *   since: 2.4.0
 *
 *   GETSET key value
 *   summary: Set the string value of a key and return its old value
 *   since: 1.0.0
 *
 *   INCR key
 *   summary: Increment the integer value of a key by one
 *   since: 1.0.0
 *
 *   INCRBY key increment
 *   summary: Increment the integer value of a key by the given amount
 *   since: 1.0.0
 *
 *   INCRBYFLOAT key increment
 *   summary: Increment the float value of a key by the given amount
 *   since: 2.6.0
 *
 *   MGET key [key ...]
 *   summary: Get the values of all the given keys
 *   since: 1.0.0
 *
 *   MSET key value [key value ...]
 *   summary: Set multiple keys to multiple values
 *   since: 1.0.1
 *
 *   MSETNX key value [key value ...]
 *   summary: Set multiple keys to multiple values, only if none of the keys exist
 *   since: 1.0.1
 *
 *   PSETEX key milliseconds value
 *   summary: Set the value and expiration in milliseconds of a key
 *   since: 2.6.0
 *
 *   SET key value [EX seconds] [PX milliseconds] [NX|XX]
 *   summary: Set the string value of a key
 *   since: 1.0.0
 *
 *   SETBIT key offset value
 *   summary: Sets or clears the bit at offset in the string value stored at key
 *   since: 2.2.0
 *
 *   SETEX key seconds value
 *   summary: Set the value and expiration of a key
 *   since: 2.0.0
 *
 *   SETNX key value
 *   summary: Set the value of a key, only if the key does not exist
 *   since: 1.0.0
 *
 *   SETRANGE key offset value
 *   summary: Overwrite part of a string at key starting at the specified offset
 *   since: 2.2.0
 *
 *   STRLEN key
 *   summary: Get the length of the value stored in a key
 *   since: 2.2.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application.properties")
@SuppressWarnings("all")
public class JedisDemo {

    private Jedis jedis;

    @Before
    public void init(){
         jedis = new Jedis("127.0.0.1", 6379);
        jedis.auth("123456");

//        JedisPoolConfig config = new JedisPoolConfig();
//        config.setMaxTotal(500);
//        config.setMaxIdle(20);
//        //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
//        config.setMaxIdle(5);
//        config.setMinIdle(1);
//        //表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
//        config.setMaxWaitMillis(1000 * 100);
//        //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
//        config.setTestOnBorrow(true);
//        JedisPool  pool = new JedisPool(config, "192.168.2.191", 6379,4,"123456");
//        jedis= pool.getResource();
    }

    @Test
    public void append(){
      jedis.set("appendjedis","abc");
        System.out.println(jedis.get("appendjedis"));
        jedis.append("appendjedis","dddd");
        System.out.println(jedis.get("appendjedis"));

    }

    @Test
    public void incr() throws InterruptedException {
        jedis.set("incr2","10");
        CountDownLatch latch = new CountDownLatch(100);
        Runnable task= new Runnable(){
            @Override
            public void run() {
                Jedis jedis = new Jedis("127.0.0.1", 6379);
                jedis.auth("123456");
                jedis.incr("incr2");
                latch.countDown();
            }
        };
        ArrayList<Thread> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Thread thread = new Thread(task,"Thread_"+i);
            list.add(thread);
        }
        list.forEach(e->{e.start();});
        latch.await();
        System.out.println(jedis.get("incr2")); //110
    }

    /**
     * 统计  比如统计 一个用户 一年登陆过多少次网站  一天登陆多次算一次
     */
    @Test
    public void setbitdemo1(){
        jedis.setbit("2021#user:1",3,"1");
        jedis.setbit("2021#user:1",5,"1");
        Long bitcount = jedis.bitcount("2021#user:1");
        System.out.println(bitcount); //2
    }

    @Test
    public void transactionSupport2() throws InterruptedException {
        jedis.watch("transactionSupport2");
        Transaction multi = jedis.multi();
        multi.set("transactionSupport2","value");
        Thread.sleep(60000L);
        multi.exec();
        jedis.unwatch();
    }
    @Test
    public void transactionSupportchange() throws InterruptedException {
        jedis = new Jedis("127.0.0.1", 6379);
        jedis.auth("123456");
        jedis.set("transactionSupport2","change");
    }


}
