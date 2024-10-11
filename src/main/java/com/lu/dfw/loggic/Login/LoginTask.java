package com.lu.dfw.loggic.Login;

import com.lu.dfw.Dao.PlayerMapper;
import com.lu.dfw.config.SpringBeanUtil;
import com.lu.dfw.entity.Player;
import com.lu.dfw.entity.PlayerLocal;
import com.lu.dfw.loggic.ATask;
import com.lu.dfw.loggic.ECode;
import com.lu.dfw.manager.LoggerManager;
import com.lu.dfw.manager.PlayerManager;
import com.lu.dfw.network.ConnectionManager;
import com.lu.dfw.proto.Login;
import com.lu.dfw.proto.Role;
import com.lu.dfw.thread.EThreadType;
import com.lu.dfw.thread.ThreadManager;

import java.util.Collections;
import java.util.Objects;

public class LoginTask extends ATask<Login.UserLoginRequest> {
    @Override
    public void execute0() {
        LoggerManager.info("收到 LoginTask %s，线程id：%s 对象 %s",request.toString(),Thread.currentThread().getName(),this);
        Player name = PlayerManager.get(request.getUsername());


        if(name == null || !Objects.equals(name.password,request.getPassword())){
            LoggerManager.info("账号不存在或密码错误");
            Login.UserLoginResponse.Builder builder = Login.UserLoginResponse.newBuilder();
            builder.setMsg("账号不存在或密码错误").setSuccess(false);
            sendMsg(builder.build().toByteArray());
            return;
        }else {
            switch (name.local){
                case Match -> {}
                case Battle -> {}
                default -> {
//                    name.local = PlayerLocal.Hall;
                }
            }
        }
        conn.player = name;
        name.local = PlayerLocal.Hall;
        //todo 到这里就可以认为是登录状态了，但是可能玩家正在匹配或者对局中，突然掉线，但是登录已经成功了，
        //就需要判断一下


        //将玩家状态置为大厅内，然后将一系列数据通过protobuf传到前端
        //注意:这是有一个规范，如果若是true则看Role，若是false看msg
        conn.player.local = PlayerLocal.Hall;

        //登录成功将玩家加入到连接管理器中
        ConnectionManager.addNetConn(conn);

        Login.UserLoginResponse.Builder builder = Login.UserLoginResponse.newBuilder();
        Role.RoleInfo info = conn.player.build();
        builder.setRole(info);
        builder.setSuccess(true);
        sendMsg(builder.build().toByteArray());
    }

    @Override
    public ECode getCode() {
        return ECode.Login;
    }

    @Override
    public int getThreadIndex() {
        String username = request.getUsername();
        int hash = Objects.hash(username);
        return ThreadManager.getThreadIndex(EThreadType.Login, hash);
    }
}
