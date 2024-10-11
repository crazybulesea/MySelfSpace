package com.lu.dfw.entity;

import com.lu.dfw.manager.RoomManager;
import com.lu.dfw.proto.Role;

public class Player {

    // 唯一ID
    public int uid;
    public String username;
    public String password;
    public String nickname;
    public int model;           // 角色模型 形象
    public int lv;
    public String createTime;
    ////////////////////////////////////////////////////////////////
    // 上次进入游戏 时间
    public long lastLogin;

    public long lastLeave;
    // 所在位置
    public PlayerLocal local;

    public PlayerBattleData battleData = new PlayerBattleData();

     //对局线程
    public RoomManager room;


    public Role.RoleInfo build() {
        Role.RoleInfo.Builder builder = Role.RoleInfo.newBuilder();
        return builder.setNickname(nickname).setId(uid).setModel(model).build();
    }


    /**
     * 玩家因为各种原因,离开了游戏
     */
    public void leave() {
        // 存盘

    }
}