package com.lu.dfw.demo;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockDemo {
    public static void main(String[] args) {

    }
}

class Ticket{
    private int num = 20;

    Lock lock = new ReentrantLock();//可重入锁
    public void sale(){
        lock.lock();
        try {
            if (num > 0) {
                System.out.println(Thread.currentThread().getName() + "卖出了第" + (num--) + "张票，剩余" + num);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();//执行完就解锁
        }
    }
}

