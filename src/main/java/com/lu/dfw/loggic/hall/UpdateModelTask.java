package com.lu.dfw.loggic.hall;

import com.lu.dfw.config.SpringBeanUtil;
import com.lu.dfw.Dao.PlayerMapper;
import com.lu.dfw.loggic.ECode;
import com.lu.dfw.proto.Login;

/**
 * 玩家更改 角色形象
 */
public class UpdateModelTask extends HallTask<Login.UserUpdateModelRequest> {
    @Override
    public void execute0() {
        player.model = request.getModel();
        PlayerMapper mapper = SpringBeanUtil.getBean(PlayerMapper.class);
        mapper.update(player);

        Login.UserUpdateModelResponse.Builder builder = Login.UserUpdateModelResponse.newBuilder();
        Login.UserUpdateModelResponse build = builder.setModel(request.getModel()).build();
        sendMsg(build.toByteArray());
    }

    @Override
    public ECode getCode() {
        return ECode.UpdateModel;
    }
}
