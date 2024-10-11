package com.lu.dfw.network;


import com.lu.dfw.entity.Player;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

public class Connection {

    public Player player;

    ChannelHandlerContext ctx;

    public Connection(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public void send(byte[] msg) {
        if (ctx != null) {
//            ctx.writeAndFlush(msg);
            ctx.writeAndFlush(msg).addListener((ChannelFuture writeFuture) -> {//ChannelFuture:操作的结果或完成的通知
                if (writeFuture.isSuccess()) {
                    //...
                     System.out.println("isSuccess");
                }
                //消息发送失败
                else {
                     System.out.println("发射失败");

                }
            });
        }
    }
}
/***
 .addListener((ChannelFuture writeFuture) -> {
 //消息发送成功
 if (writeFuture.isSuccess()) {
 //...
 System.out.println("isSuccess");
 }
 //消息发送失败
 else {
 System.out.println("发射失败");

 }
 })
 *
 */