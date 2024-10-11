package com.lu.dfw.loggic;

/**
 * @Author
 * @Description 玩家单人操作使用100-499,队伍使用500-999;公会使用1000-1499;其他后续补充
 * @Version 1.0
 */
public enum ECode {
    Non(0),
    Register(1),          // 注册
    Login(2),             // 登录
    UpdateNickname(3),   // 昵称
    UpdateModel(4),     // 更改模型 形象

    LeaveGame(9),        // 离开游戏



    Matching(10),      // 申请匹配
    Look(11),           // 申请观战

    LeaveBattle(20),    // 离开了对局场景



    BattleBegin(50),    // 通知客户端,对局开启; 需要通知 客户端 双方的 昵称,ID,等数据
    SendRollRoll(51),       // 通知 所有玩家,该某个角色 roll了
    RoleRoll(52),           // 请求roll点 以及返回
    MoveTargetEnd(53),      // 玩家通知服务器,自己到达位置了

    FuncRequest(54),        // 根据玩家在 所在的位置 发出请求;升级建筑/建筑新的建筑/购买道具等

    BattleShop(55),         // 商店购买对局道具
    BattleUseItem(56),      // 对局使用了道具

    BattleOver(60),         // 某个玩家对局结束
    Relink(61),             // 重连
    ;

    private int code;

    ECode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ECode getType(int code) {
        ECode[] values = values();
        for (int i = 0; i < values.length; i++) {
            if (code == values[i].code) {
                return values[i];
            }
        }
        return Non;
    }


}
