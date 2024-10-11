package com.lu.dfw.loggic;

import com.lu.dfw.loggic.battle.*;
//import com.lu.dfw.loggic.hall.*;
//import com.lu.dfw.loggic.login.*;

import com.lu.dfw.loggic.Login.LoginTask;
import com.lu.dfw.loggic.hall.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import com.lu.dfw.loggic.Login.RegisterTask;

/**
 * @Author qzj
 * @Description  产生 处理 task任务对象类的 工厂; 新增的 任务,需要在这里注册; 只需要注册 客户端请求处理的
 * @Version 1.0
 */
public class TaskFactory {

    //key：消息号；value：任务对象类
    private static Map<Integer, Supplier<ATask>> taskMap = new ConcurrentHashMap<>(64);

    public static void init() {
        taskMap.put(ECode.Login.getCode(), LoginTask::new);
        taskMap.put(ECode.Register.getCode(), RegisterTask::new);
        taskMap.put(ECode.UpdateNickname.getCode(), UpdateNickname::new);
        taskMap.put(ECode.UpdateModel.getCode(), UpdateModelTask::new);
//        taskMap.put(ECode.Look.getCode(), LookTask::new);
        taskMap.put(ECode.Matching.getCode(), MatchingTask::new);
        taskMap.put(ECode.RoleRoll.getCode(), RoleRollTask::new);
        taskMap.put(ECode.MoveTargetEnd.getCode(), MoveTargetEndTask::new);
        taskMap.put(ECode.FuncRequest.getCode(), FuncRequestTask::new);
//        taskMap.put(ECode.BattleShop.getCode(), BattleShopTask::new);
//
//
//        taskMap.put(ECode.LeaveBattle.getCode(), LeaveBattleTask::new);

    }


    public static ATask get(int code) {
        Supplier<ATask> supplier = taskMap.get(code);
        if (supplier == null) {
            return null;
        }
        return supplier.get();
    }


}
