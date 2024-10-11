package com.lu.dfw.loggic.battle;

import com.lu.dfw.loggic.BattleTask;
import com.lu.dfw.loggic.ECode;
import com.lu.dfw.proto.Battle;

/**
 * @author 卢博文 
 * @version 1.0
 * @description
 */
public class BattleShopTask extends BattleTask<Battle.BattleShopRequest> {
    @Override
    public ECode getCode() {
        return ECode.BattleShop;
    }

    @Override
    public void execute1() {
        player.room.battleShop(player, request.getCid());
    }
}
