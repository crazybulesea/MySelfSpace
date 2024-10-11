package com.lu.dfw.network;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author
 * @Data 2021/12/26 15:29
 * @Description
 * @Version 1.0
 */
public class ConnectionManager {
    private static Map<ChannelHandlerContext, Connection> ctxs = new ConcurrentHashMap<>();

    private static Map<Integer, Connection> players = new ConcurrentHashMap<>();

    public static Connection getNetConn(ChannelHandlerContext ctx) {
        return ctxs.get(ctx);
    }

    public static Connection getNetConn(int uid) {
        return players.get(uid);
    }

    public static void addNetConn(Connection conn) {
        ctxs.put(conn.ctx, conn);
        if (conn.player != null) {
            players.put(conn.player.uid, conn);
        }
    }


    public static void remove(int uid) {
        Connection netConn = players.remove(uid);
        if (netConn != null) {
            ctxs.remove(netConn.ctx);
            netConn.ctx.flush();
            netConn.ctx.close();
        }
    }

    public static void remove(ChannelHandlerContext ctx) {
        Connection netConn = ctxs.remove(ctx);
        if (netConn != null && netConn.player != null) {
            players.remove(netConn.player.uid);
        }
        ctx.flush();
        ctx.close();
    }

}
