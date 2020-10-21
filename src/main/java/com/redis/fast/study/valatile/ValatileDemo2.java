package com.redis.fast.study.valatile;

/**
 * @Author: liulang
 * @Date: 2020/10/15 15:17
 */
@SuppressWarnings("all")
public class ValatileDemo2 {

    public static volatile int race = 0;
    public static  void increase(){
        race++;
    }

//    public static synchronized void increase(){
//        race++;
//    }

    private static final int THREADS_COUNT = 20;

    public static void main(String[] args){
        System.out.println(Thread.activeCount());
        Thread[] threads = new Thread[ THREADS_COUNT ];
        for(int i=0; i<THREADS_COUNT; i++){
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for(int j=0; j<10000; j++) {
                        increase();
                    }
                }
            });

            threads[i].start();
        }

        while(Thread.activeCount() > 2){
//            System.out.println(Thread.activeCount());
//            System.out.println("111111111");
            Thread.yield();
        }
        System.out.println(race);
    }

}
