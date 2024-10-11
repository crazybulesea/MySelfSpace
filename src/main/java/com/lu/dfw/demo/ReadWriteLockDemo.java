package com.lu.dfw.demo;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockDemo {
    public static void main(String[] args){
        Mycache2 mycache = new Mycache2();
        //写入
        for(int i = 1; i <= 5; i++){
            final int tempInt = i;
            new Thread(() -> {
                mycache.put(tempInt + "", tempInt + "");
            }, "Thread" + i).start();
        }

        //读取
        for(int i = 1; i <= 5; i++){
            final int tempInt = i;
            new Thread(() -> {
                mycache.get(tempInt + "");
            }, "Thread" + i).start();
        }
    }
}

class Mycache{
    private volatile Map<String, Object> map = new HashMap<>();

    public void put(String key, Object value){
        System.out.println(Thread.currentThread().getName() + "正在写入" + key);
        map.put(key, value);
        System.out.println(Thread.currentThread().getName() + "写入完成");
    }

    public void get(String key){
        System.out.println(Thread.currentThread().getName() + "正在读取" + key);
        Object o = map.get(key);
        System.out.println(Thread.currentThread().getName() + "读取完成");

    }
}

class Mycache2{
    private volatile Map<String, Object> map = new HashMap<>();

    //更加细粒度的操作，读不加锁，写加锁
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public void put(String key, Object value){
        lock.writeLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + "正在写入" + key);
            map.put(key, value);
            System.out.println(Thread.currentThread().getName() + "写入完成");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.writeLock().unlock();
        }
    }

    public void get(String key){
        lock.readLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + "正在读取" + key);
            Object o = map.get(key);
            System.out.println(Thread.currentThread().getName() + "读取完成");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.readLock().unlock();
        }
    }
}