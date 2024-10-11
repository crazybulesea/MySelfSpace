package com.lu.dfw.loggic.battle;

import com.lu.dfw.loggic.ATask;
import com.lu.dfw.loggic.ECode;
import com.lu.dfw.proto.Hall;
import com.lu.dfw.thread.EThreadType;
import com.lu.dfw.thread.ThreadManager;

/**
 * @author 卢博文 
 * @version 1.0
 * @description
 */
public class LeaveBattleTask extends ATask<Hall.LeaveBattleRequest> {
    @Override
    public ECode getCode() {
        return ECode.LeaveBattle;
    }

    @Override
    public int getThreadIndex() {
        if (player.room == null) {
            return ThreadManager.getThreadIndex(EThreadType.Match, player.uid);
        }
        return ThreadManager.getThreadIndex(EThreadType.Battle, player.room.threadId);
    }

    @Override
    public void execute0() {
        if (player.room == null) {
            Hall.LeaveBattleResponse build = Hall.LeaveBattleResponse.newBuilder().build();
            ATask.sendMsgToOne(player.uid, ECode.LeaveBattle, build.toByteArray());
        } else {
            player.room.leave(player);
        }
    }
}
