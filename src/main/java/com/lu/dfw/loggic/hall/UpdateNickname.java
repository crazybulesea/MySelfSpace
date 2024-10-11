package com.lu.dfw.loggic.hall;

import com.lu.dfw.Dao.PlayerMapper;
import com.lu.dfw.config.SpringBeanUtil;
import com.lu.dfw.loggic.ECode;
import com.lu.dfw.manager.LoggerManager;
import com.lu.dfw.proto.Login;
import com.lu.dfw.thread.EThreadType;
import com.lu.dfw.thread.ThreadManager;

public class UpdateNickname extends HallTask<Login.UserUpdateNicknameRequest>{
    @Override
    public void execute0() {
        LoggerManager.info("UpdateNickname");
        PlayerMapper mapper = SpringBeanUtil.getBean(PlayerMapper.class);

        int count = mapper.getNickname(request.getNickname());
        Login.UserUpdateNicknameResponse.Builder response = Login.UserUpdateNicknameResponse.newBuilder();
        if(count > 0){
            response.setMsg("昵称已存在").setSuccess(false);
            sendMsg(response.build().toByteArray());
        }else {
            player.nickname = request.getNickname();
            mapper.update(player);
            response.setSuccess(true);
            response.setNickname(request.getNickname());//成功返回当前昵称，失败返回原因
            sendMsg(response.build().toByteArray());
        }
    }

    @Override
    public ECode getCode() {
        return ECode.UpdateNickname;
    }

}
