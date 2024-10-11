package com.lu.dfw.netty;

import com.lu.dfw.manager.LoggerManager;
import com.lu.dfw.proto.Login;
import com.lu.dfw.thread.ThreadManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * I/O数据读取处理类
 */
@ChannelHandler.Sharable
public class BootNettyChannelInboundHandlerAdapter
        extends ChannelInboundHandlerAdapter {

    /**
     * 从客户端收到新的数据时，这个方法会在收到消息时被调用
     *
     * @param ctx 包含管道pipeline,通道channel,
     * @param msg 客户端发送的数据
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //收到客户端请求,交给 线程管理器 分发
        ThreadManager.pushRequest(ctx, (byte[]) msg);
        //5：1 1234
        //Login.UserRegisterRequest.Builder();

        //demo案例
//        byte[] b = (byte[])msg;
//        byte[] codeByte = new byte[]{b[0], b[1]};
//        int code = bytesToInt(codeByte);
//
//        byte[] b1 = new byte[b.length - 2];
//        for (int i = 2; i < b.length; i++){
//            b1[i - 2] = b[i];
//        }
//
//        Login.UserRegisterRequest userRegisterRequest = Login.UserRegisterRequest.parseFrom(b1);
//        System.out.println(userRegisterRequest);

//        System.out.println(codeByte);
    }

    private static int bytesToInt(byte[] bytes) {
        return (bytes[0] & 0xff) | ((bytes[1] & 0xff) << 8);
    }
    /**
     * 从客户端收到新的数据、读取完成时调用
     *
     * @param ctx
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws IOException {
        // System.out.println("channelReadComplete : 读取完成");

        // 刷新
        //ctx.writeAndFlush(Unpooled.copiedBuffer("hello~",CharsetUtil.UTF_8));
        // ctx.Flush("i go");
        ctx.flush();
    }

    /**
     * 当出现 Throwable 对象才会被调用，即当 Netty 由于 IO 错误或者处理器在处理事件时抛出的异常时
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws IOException {
        try {
            cause.printStackTrace();
            ctx.flush();
        } catch (Exception e) {
            System.out.println("exceptionCaught...");
        }

        // ctx.close();//抛出异常，断开与客户端的连接
    }

    /**
     * 客户端与服务端第一次建立连接时 执行
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception, IOException {
        super.channelActive(ctx);
        ctx.channel().read();
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = insocket.getAddress().getHostAddress();
        LoggerManager.info("用户ip: " + clientIp + " 连接到了服务器" + insocket.getPort());
        // 此处可以 做黑名单 处理
//        boolean connection = BlackListManager.connection(clientIp);
//        // 如果在黑名单,直接拒绝
//        if (connection) {
//            log.info("用户ip: " + clientIp + " 在黑名单,拒绝了本次登录");
//            ctx.close();
//            return;
//        }
//
//        if (!GameStatusManager.canLogin()) {
//            log.info("用户ip: " + clientIp + " 在维护时 强行登陆");
//            ctx.close();
//            return;
//        }
        ctx.flush();
    }

    /**
     * 客户端与服务端 断连时 执行
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception, IOException {
//        super.channelInactive(ctx);
//        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
//        String clientIp = insocket.getAddress().getHostAddress();



        // TODO 断开连接 处理
//        Connection connection = ConnectionManager.getNetConn(ctx);
//        if (connection != null && connection.player != null) {
//            connection.player.leave();
//            connection.player.lastLeave = System.currentTimeMillis();
//        }
//        System.out.println("与客户端断开连接: " + ctx);
//        ConnectionManager.remove(ctx);

        //ctx.close(); //断开连接时，必须关闭，否则造成资源浪费，并发量很大情况下可能造成宕机
    }

    /**
     * 服务端当read超时, 会调用这个方法
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception, IOException {
        // InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        // String clientIp = insocket.getAddress().getHostAddress();
        // ctx.close();//超时时断开连接
        // ctx.flush();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        LoggerManager.info("channelRegistered");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
        //ctx.close(); 从EventLoop注销并且无法处理任何I/O时被调用
        LoggerManager.info("channelUnregistered");
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        LoggerManager.info("channelWritabilityChanged");
    }

}