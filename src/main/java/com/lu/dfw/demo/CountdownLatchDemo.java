package com.lu.dfw.demo;

import java.util.concurrent.CountDownLatch;

public class CountdownLatchDemo {
    public static void main(String[] args) throws InterruptedException {
        //总数为6，必须要执行任务的时候再使用。
        CountDownLatch latch = new CountDownLatch(6);

        for(int i = 0; i < 6; i++){
            new Thread(()->{
                System.out.println(Thread.currentThread().getName() + " come in");
                latch.countDown();
            }).start();
        }

        latch.await();//等计数器归零，然后再向下执行

        System.out.println("close");
    }
}
