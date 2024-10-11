package com.lu.dfw.config;

/**
 * @Author qzj
 * @Description 适合 配置在 xml中,启动时读取
 * @Version 1.0
 */
public interface Const {

    // 端口号
    int Port = 11111;

    // 注册/登录 线程数
    int Login = 1;
    // 大厅通用线程数
    int Hall = 4;
    // 匹配专用线程数
    int Match = 1;
    // 对局线程数
    int Game = 16;

    // 一个线程,管理的最大房间数
    int RoomNum = 100;

    int AwaitTime = 11; // 等待玩家操作时间,10秒,多给玩家1秒

    int AwaitShop = 31; // 等待玩家购买物品 时间,30秒,多给玩家1秒

}
