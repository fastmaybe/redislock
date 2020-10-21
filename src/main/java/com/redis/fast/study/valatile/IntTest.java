package com.redis.fast.study.valatile;

import java.lang.reflect.Field;

/**
 * @Author: liulang
 * @Date: 2020/10/15 14:43
 */
@SuppressWarnings("all")
public class IntTest {


    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
//        test1();
        test2();

    }


    /**
     * 两个 Integer 在 -128  127之间是缓存
     * @param args
     */
    public static void  test1(){
        Integer a =127 ,b =127;
        Integer c =128 ,d =128;

        System.out.println(a.equals(b));
        System.out.println(a==b);
        System.out.println("=============");
        System.out.println(c.equals(d));
        System.out.println(c==d);

    }

    public static void  test2() throws NoSuchFieldException, IllegalAccessException {
        Integer a =127 ,b =127;
        System.out.println(a==b);
        System.out.println("before a="+a +", b="+b);
        swap(a,b);
        System.out.println("after a="+a +", b="+b);

    }

    public static  void  swap(Integer i1, Integer i2) throws NoSuchFieldException, IllegalAccessException {
        Field field = Integer.class.getDeclaredField("value");
        field.setAccessible(true);
        Integer tmp = new Integer(i1.intValue());
        field.set(i1,i2.intValue());
        field.set(i2,tmp);
    }

}

