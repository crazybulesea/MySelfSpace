package com.lu.dfw.thread;

import com.lu.dfw.manager.LoggerManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

//多线程
public class Threads extends Thread {

    private boolean runFlag = true;

    //重点
    private final BlockingQueue<ITask> tasks = new LinkedBlockingQueue<>();


    public Threads(String name) {
        super.setName(name);
    }

    /**
     *
     * @param name 线程名
     * @param priority 优先级,1-10,10最高
     */
    public Threads(String name, int priority) {
        super.setName(name);
        super.setPriority(priority);
    }

    // 退出线程
    public final void setExit() {
//        ThreadTask task = new ThreadTask();
        runFlag = false;
        push(new ThreadTask() {
            @Override
            public void execute() {

            }
        });
    }


    public void push(ITask task) {
        if (task == null) {
            return;
        }
        tasks.add(task);
    }


    // 查看当前线程 任务堆积
    public int getTaskLength() {
        return tasks.size();
    }

    @Override
    public void run() {
        while (runFlag) {
            try {
                // 阻塞等待, 直到有任务,有任务就执行
                ITask take = tasks.take();
                take.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        LoggerManager.info(getName() + " 线程已关闭");

    }
}
