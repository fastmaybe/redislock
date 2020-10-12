package com.redis.fast.aspect;

import com.redis.fast.utils.RedisLockUtil;
import lombok.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author: liulang
 * @Date: 2020/10/12 16:15
 */
@Aspect
@Component
public class LockAspectImpl {

    public static final Logger logger = LoggerFactory.getLogger(LockAspectImpl.class);

    @Autowired
    public RedisLockUtil redisLock;

    @Pointcut("@annotation(LockAnnotation)")
    public void pointCut(){
    }

    @Around("pointCut() && @annotation(lockAnnotation)")
    public Object around(ProceedingJoinPoint joinPoint,LockAnnotation lockAnnotation) throws Throwable {
        String lockKey = lockAnnotation.lockKey();
        if (StringUtils.isEmpty(lockKey)){
            lockKey = joinPoint.getSignature().getName();
        }

        //锁 钥匙
        String lock = lockAnnotation.lockPre() + lockKey;
        String value = UUID.randomUUID().toString().replace("-", "");


        //时间单位
        TimeUnit unit = lockAnnotation.unit();
        //锁时长
        long lockTime = lockAnnotation.lockTime();
        long lockTimeSeconds = unit.toSeconds(lockTime);

        //看门狗
        boolean watchDog = lockAnnotation.watchDog();

        long waitTimeSeconds = unit.toSeconds(lockAnnotation.waitTime());
        long endTime = System.currentTimeMillis() + waitTimeSeconds * 1000L;


        try {
            do {
                if (redisLock.lock(lock,value,lockTime,unit)){
                    if (watchDog){
                        SurvivalClamProcessor sp = new SurvivalClamProcessor();
                        sp.setLock(lock);
                        sp.setLockTime(lockTimeSeconds);
                        sp.setLockValue(value);

                        Thread st = new Thread(sp);
                        st.setDaemon(true);
                        st.start();
                    }
                    return joinPoint.proceed();

                }
                //600 = 3 * 1000 * 1/5    200 = 1* 1000 * 1/5
                long sleepTime = Math.min(600, Math.min(waitTimeSeconds, lockTimeSeconds) * 200);
                Thread.sleep(sleepTime);
            }while (System.currentTimeMillis() <= endTime);

        }finally {
            Boolean releaseLock = redisLock.releaseLock(lock, value);
            logger.info(Thread.currentThread().getName()+" 释放锁结果 = 》 {}",releaseLock);
        }
        return null;
    }




    @Setter
    @Getter
    public class SurvivalClamProcessor implements Runnable{
        private String lock;
        private String lockValue;
        private long lockTime;
        private volatile boolean signal;

        {
            this.signal = true;
        }

        private void stop(){
            this.signal  = false;
        }

        public void run() {
            val waitTime = (lockTime * 1000 * 2 / 3);

            while (signal){

                try {
                    Thread.sleep(waitTime);
                    if (redisLock.expandLockTime(lock,lockValue,lockTime)){
                        logger.info("expandLockTime 成功，本次等待{}ms，将重置锁超时时间重置为{}s,  其中lock为{}   ;  lockValue {}", waitTime, lockTime, lock, lockValue);
                    }else {
                        this.stop();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
    }

}
