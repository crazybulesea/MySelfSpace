package com.lu.dfw.entity;

import com.lu.dfw.proto.Role;

/**
 * @author 卢博文 
 * @version 1.0
 * @description 一个格子的信息
 */
public class Grid {

    // 类型; 0:空地, 1:事件点,2:商店;  收费建筑: 3商场,4宾馆,5饭店
    public int type;
    // 所属玩家在房间中的下标;
    public int index;
    // 建筑类等级
    public int lv;

    public Grid(int type) {
        this.type = type;
    }

    public Role.GridInfo build(){
        Role.GridInfo.Builder builder = Role.GridInfo.newBuilder();
        return builder.setIndex(index).setLv(lv).setType(type).build();
    }

}
