package com.lu.dfw.loggic.battle;

import com.lu.dfw.loggic.BattleTask;
import com.lu.dfw.loggic.ECode;
import com.lu.dfw.proto.Battle;

/**
 * 请求roll点
 */
public class RoleRollTask extends BattleTask<Battle.RoleRollRequest> {
    @Override
    public void execute1() {

        player.room.playerRoll(player, request.getId());

    }

    @Override
    public ECode getCode() {
        return ECode.RoleRoll;
    }
}
