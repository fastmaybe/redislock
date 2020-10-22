package com.redis.fast.redis_API.string;

import com.redis.fast.pojo.User;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
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
public class RedisTemplateDemo {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void append(){
        redisTemplate.opsForValue().set("a","abc");
        Object a = redisTemplate.opsForValue().get("a");
        System.out.println("old value=>"+a);
        redisTemplate.opsForValue().append("a","bc");
        Object newa = redisTemplate.opsForValue().get("a");
        System.out.println("new value=>"+newa);
    }


    /**
     * 计数器
     */
    @Test
    public void INCR() throws InterruptedException {
        redisTemplate.opsForValue().set("incr1","9");
        CountDownLatch latch = new CountDownLatch(100);
        Runnable task= new Runnable(){
            @Override
            public void run() {
                redisTemplate.opsForValue().increment("incr1");
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

        System.out.println(redisTemplate.opsForValue().get("incr1"));//109
    }

    /**
     * 统计  Bitmap
     * Bitmap是一串连续的2进制数字（0或1），每一位所在的位置为偏移(offset)，在bitmap上可执行AND,OR,XOR以及其它位操作。
     * 一个字节 8个位
     * 比如   10000000 10101001
     *
     * https://blog.csdn.net/weixin_34061042/article/details/85674403
     */

    /**
     * 统计  比如统计 一个用户 一年登陆过多少次网站  一天登陆多次算一次
     */
    @Test
    public void setbitdemo1(){
        //用户 标识 key => user:1  (年份#用户标识)
        //offset 一年的第几天
        //在一年的 第五天登陆
        redisTemplate.opsForValue().setBit("2020#user:1",4,true);
        //在一年的 第10天登陆
        redisTemplate.opsForValue().setBit("2020#user:1",9,true);

        Object execute = redisTemplate.execute((RedisCallback<Long>) con -> con.bitCount("2020#user:1".getBytes()));
        // 2
        System.out.println(execute);

    }

    /**
     * 统计活跃用户
     */
    @Test
    public void setBitdemo2(){
        //2021 1021 这天 3 和 4代表的用户 活跃了
        redisTemplate.opsForValue().setBit("20201021",3,true);
        redisTemplate.opsForValue().setBit("20201021",4,true);
        //2021 1022 这天 3 代表的用户 活跃了
        redisTemplate.opsForValue().setBit("20201022",3,true);
        //2021 1023 这天 5 代表的用户 活跃了
        redisTemplate.opsForValue().setBit("20201023",5,true);
        redisTemplate.opsForValue().setBit("20201023",3,true);

        //统计这几天只要活跃过的用户  总数数量
        byte[][] bytess = new byte[][]{"20201021".getBytes(),"20201022".getBytes(),"20201023".getBytes()};
        redisTemplate.execute((RedisCallback) con->con.bitOp(RedisStringCommands.BitOperation.OR,"jedisresult2".getBytes(),bytess));
        Object execute = redisTemplate.execute((RedisCallback) con -> con.bitCount("jedisresult2".getBytes()));

        System.err.println(execute);  //3


        //统计这几天一直在的  用户数量
        //统计这几天只要活跃过的用户  总数

        redisTemplate.execute((RedisCallback) con->con.bitOp(RedisStringCommands.BitOperation.AND,"jedisresult3".getBytes(),bytess));
        Object execute2 = redisTemplate.execute((RedisCallback) con -> con.bitCount("jedisresult3".getBytes()));
        System.err.println(execute2);  //1

    }

//    public Long bitCount(String key) {
//        return redisTemplate.execute((RedisCallback<Long>) con -> con.bitCount(key.getBytes()));
//    }
//
//    public Long bitCount(String key, int start, int end) {
//        return redisTemplate.execute((RedisCallback<Long>) con -> con.bitCount(key.getBytes(), start, end));
//    }
//
//    public Long bitOp(RedisStringCommands.BitOperation op, String saveKey, String... desKey) {
//        byte[][] bytes = new byte[desKey.length][];
//        for (int i = 0; i < desKey.length; i++) {
//            bytes[i] = desKey[i].getBytes();
//        }
//        return redisTemplate.execute((RedisCallback<Long>) con -> con.bitOp(op, saveKey.getBytes(), bytes));
//    }


    @Test
    public void transactionSupport(){

        String key = "transaction1";


        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                try {
                    redisOperations.multi();
                    redisOperations.opsForValue().set(key,"value");
                    int bb = 10/0;
                    List exec = redisOperations.exec();
                } catch (Exception e) {
                    redisOperations.unwatch();
                    e.printStackTrace();
                }
                return null;
            }
        });
    }


}
