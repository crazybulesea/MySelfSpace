package com.lu.dfw.loggic.Login;

import com.lu.dfw.Dao.PlayerMapper;
import com.lu.dfw.config.SpringBeanUtil;
import com.lu.dfw.entity.Player;
import com.lu.dfw.entity.PlayerLocal;
import com.lu.dfw.loggic.ATask;
import com.lu.dfw.loggic.ECode;
import com.lu.dfw.manager.PlayerManager;
import com.lu.dfw.network.ConnectionManager;
import com.lu.dfw.proto.Login;
import com.lu.dfw.thread.EThreadType;
import com.lu.dfw.thread.ThreadManager;

import java.util.Objects;
import java.util.Random;

public class RegisterTask extends ATask<Login.UserRegisterRequest> {
    @Override
    public void execute0() {
//        PlayerMapper mapper = SpringBeanUtil.getBean(PlayerMapper.class);
//        Player player = mapper.getPlayerByName(request.getUsername());
        Player player = PlayerManager.get(request.getUsername());
        if(player == null){
            player = new Player();
            player.username = request.getUsername();
            player.password = request.getPassword();
            String random = Integer.toString(new Random().nextInt(0,10000));
            player.nickname = "用户" + random;
            PlayerManager.add(player);

            Login.UserRegisterResponse.Builder response = Login.UserRegisterResponse.newBuilder();//proto转换
            Login.UserRegisterResponse bulid = response.setMsg("注册成功").setSuccess(true).build();

            conn.player = player;
            conn.player.local = PlayerLocal.Register;

            sendMsg(bulid.toByteArray());
        }else{
            Login.UserRegisterResponse.Builder errResponse = Login.UserRegisterResponse.newBuilder();
            Login.UserRegisterResponse err = errResponse.setMsg("用户名已存在").setSuccess(false).build();
            sendMsg(err.toByteArray());
        }
    }

    @Override
    public ECode getCode() {
        return ECode.Register;
    }

    @Override
    public int getThreadIndex() {
        String username = request.getUsername();
        int hash = Objects.hash(username);
        return ThreadManager.getThreadIndex(EThreadType.Login, hash);
    }
}
