package com.lu.dfw.loggic.battle;

import com.lu.dfw.loggic.BattleTask;
import com.lu.dfw.loggic.ECode;
import com.lu.dfw.proto.Battle;

/**
 * @author 卢博文 
 * @version 1.0
 * @description 客户端通知 服务器,自己打到目标位置了
 */
public class MoveTargetEndTask extends BattleTask<Battle.MoveTargetEndRequest> {
    @Override
    public void execute1() {
        player.room.moveEnd(player);
    }

    @Override
    public ECode getCode() {
        return ECode.MoveTargetEnd;
    }
}
