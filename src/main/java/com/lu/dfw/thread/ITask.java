package com.lu.dfw.thread;

/**
 * @Author
 * @Data 2022/11/9 21:52
 * @Description
 * @Version 1.0
 */
public interface ITask {

    // 执行任务
    void execute();

    // 获取执行的线程
    int getThreadIndex();

}
