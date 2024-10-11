package com.lu.dfw.demo;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class CallableTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Mycallable mycallable=new Mycallable();
        FutureTask<String> futureTask=new FutureTask<String>(mycallable);
        new Thread(futureTask,"A").start();
        new Thread(futureTask,"B").start();//

        // 获取线程返回结果
        String result = futureTask.get();
        System.out.println(result);
    }
}

class Mycallable implements Callable<String>{//string相当于定义的返回值的数据类型
    @Override
    public String call(){
        System.out.println("callable");
        return "123456";
    }
}