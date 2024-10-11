package com.lu.dfw.manager;

/**
 * 当前行动的玩家 对局状态
 */
public enum BattleStatus {
    Non,
    /**
     * 等待通知当前行动的玩家 roll
     */
    AwaitRoll,
    /**
     * 已通知 玩家可以 roll 了,处于 等待 玩家点击 roll的时间
     */
    AwaitRollSend,

    /**
     * roll点后,移动中
     */
    MoveIng,
    /**
     * 移动结束,等玩家 操作
     */
    AwaitSel,

    AwaitShop,      // 商店单独处理



}
