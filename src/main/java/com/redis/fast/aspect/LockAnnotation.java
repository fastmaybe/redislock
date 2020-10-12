package com.redis.fast.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @Author: liulang
 * @Date: 2020/10/12 16:09
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@SuppressWarnings("unchecked")
public @interface LockAnnotation {

    String lockPre() default   "lubanLock:";
    String lockKey() default  "";
    long lockTime() default  3L;
    long waitTime() default  30L;

    TimeUnit unit() default TimeUnit.SECONDS;
    boolean watchDog() default  false;

    /**
     * lockPre ： 锁前缀 可自定义 默认值为上图
     * lockKey： 锁向量 如没有 默认值取方法名
     * lockTime ： 锁住的时间 默认值3s
     * waitTime： 等待的时间 超过时间没有抢到锁  直接放弃执行 ；默认值30s
     *              waitTime 根据业务场景选择
     *              场景一：waitTime 设置为0 意味没有抢到锁 直接放弃  适用 分布式定时任务 互斥
     *              场景二：业务耗时预估 < 3s ; lockTime 3s  并发10  如果waitTime 设置小于 3 *（10-1）s ；会有任务丢失
     *                      如想保证一定执行 waitTime 的值设置为比较大的值 > lockTime * 比较高并发量
     *
     *
     *
     * timeUnit： 时间单位
     * watchDog ： 看门狗守护线程  默认为 false 不开启  场景 为  业务执行还没有完 锁却过期了
     */

    /**
     * 使用注意  1 扫描包
     *          2 redis配置
     *
     */
    //如果处于redis集群模式 有些问题需要考虑 数据同步 宕机的问题 ；如果处于集群建议直接集成redssion
}
