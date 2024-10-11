package com.lu.dfw.netty;

import com.lu.dfw.config.Const;
import com.lu.dfw.manager.LoggerManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class BootNettyServer {
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public void bind(int port) {

        /**
         * 配置服务端的NIO线程组
         * NioEventLoopGroup 是用来处理I/O操作的Reactor线程组
         * bossGroup：用来接收进来的连接
         * workerGroup的EventLoopGroup默认的线程数是CPU核数的二倍
         */
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup(8);

        try {
            /**
             * ServerBootstrap 是一个启动NIO服务的辅助启动类
             */
            ServerBootstrap bootstrap = new ServerBootstrap();
            /**
             * 设置group，将bossGroup， workerGroup线程组传递到ServerBootstrap
             */
            bootstrap = bootstrap.group(bossGroup, workerGroup);
            /**
             * ServerSocketChannel是以NIO的selector为基础进行实现的，用来接收新的连接，这里告诉Channel通过NioServerSocketChannel获取新的连接
             */
            bootstrap = bootstrap.channel(NioServerSocketChannel.class);

            // Nagle 算法 是否关闭;这个算法会 堆积一定数据才发送给客户端,影响游戏 即时性;true表示 关闭
            bootstrap = bootstrap.childOption(ChannelOption.TCP_NODELAY, true);

            BootNettyChannelInboundHandlerAdapter handlerAdapter = new BootNettyChannelInboundHandlerAdapter();
            MyEncoder myEncoder = new MyEncoder();

            bootstrap = bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    // 发送消息前,编码
                    ch.pipeline().addLast(myEncoder);
                    // 收到消息后,解码
                    ch.pipeline().addLast(new com.lu.dfw.netty.MyDecoder());

                    // 业务处理
                    ch.pipeline().addLast(handlerAdapter);
                }
            });

            LoggerManager.info("netty server start success, Port: " + Const.Port);
            /**
             * 绑定端口，并且同步,生成ChannelFuture对象,这里已经启动服务器
             */
            ChannelFuture cf = bootstrap.bind(port).sync();


            /**
             * 对关闭通道进行监听
             */
            cf.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            LoggerManager.error(e.toString());
            // e.printStackTrace();
        } finally {
            exit();
        }
    }

    public void exit() {
        LoggerManager.error("netty 被结束,释放资源...");
        /**
         * 退出，释放线程池资源
         */
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }


}
