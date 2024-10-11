package com.lu.dfw.loggic.battle;

import com.lu.dfw.loggic.BattleTask;
import com.lu.dfw.loggic.ECode;
import com.lu.dfw.proto.Battle;

/**
 * @author 卢博文 
 * @version 1.0
 * @description
 */
public class FuncRequestTask extends BattleTask<Battle.FuncRequest> {
    @Override
    public ECode getCode() {
        return ECode.FuncRequest;
    }

    @Override
    public void execute1() {
        player.room.awaitSel(player, request.getEvent());
    }
}
