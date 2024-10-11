package com.lu.dfw.thread;


/**
 * 只执行任务,忽略 消息号的 task
 */
public abstract class ThreadTask implements ITask {

    @Override
    public int getThreadIndex() {
        return 0;
    }
}
