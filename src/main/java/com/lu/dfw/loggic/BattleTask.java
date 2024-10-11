package com.lu.dfw.loggic;

import com.lu.dfw.thread.EThreadType;
import com.lu.dfw.thread.ThreadManager;

/**
 * @Author qzj
 * @Description
 * @Version 1.0
 */
public abstract class BattleTask<T> extends ATask<T> {

    @Override
    public void execute0() {
        if (player.room == null) {
            return;
        }
        execute1();
    }

    public abstract void execute1();


    public int getThreadIndex() {
        return ThreadManager.getThreadIndex(EThreadType.Battle, player.room.threadId);
    }

}
