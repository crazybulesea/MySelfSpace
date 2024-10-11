package com.lu.dfw.loggic.hall;


import com.lu.dfw.loggic.ECode;
import com.lu.dfw.manager.MatchingManager;
import com.lu.dfw.proto.Hall;
import com.lu.dfw.thread.EThreadType;
import com.lu.dfw.thread.ThreadManager;

// 申请匹配对局, 加入/离开 匹配,使用匹配线程
public class MatchingTask extends HallTask<Hall.MatchingRequest> {
    @Override
    public void execute0() {
        Hall.MatchingResponse.Builder builder = Hall.MatchingResponse.newBuilder();
        //这里就是对数据的封装，如果前端要求退出匹配，后端调用leave方法，退出匹配
        if (request.getType() == -1) {
            boolean leave = MatchingManager.leave(player);
            builder.setType(!leave);
        } else {//如果前端要求加入匹配，后端调用add方法，加入匹配
            MatchingManager.add(player, request.getType());
            builder.setType(true);
        }
        sendMsg(builder.build().toByteArray());
    }

    @Override
    public ECode getCode() {
        return ECode.Matching;
    }

    public int getThreadIndex() {
        return ThreadManager.getThreadIndex(EThreadType.Match, player.uid);
    }//匹配只有一条线程，player.uid无所谓。
}
