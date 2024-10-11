package com.lu.dfw.thread;

import com.lu.dfw.config.Const;
import com.lu.dfw.loggic.ATask;
import com.lu.dfw.loggic.ECode;
import com.lu.dfw.loggic.TaskFactory;
import com.lu.dfw.manager.LoggerManager;
import com.lu.dfw.manager.MatchingManager;
import com.lu.dfw.network.Connection;
import com.lu.dfw.network.ConnectionManager;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.*;

/**
 * @Author qzj
 * @Data 2023/9/20 17:00
 * @Description 线程管理器
 * @Version 1.0
 */
public class ThreadManager {
    /**
     * 全局线程，用于处理一些全局的逻辑，如匹配管理，对局管理等
     */
    public static ExecutorService global = Executors.newSingleThreadExecutor();

    // 所有逻辑任务处理线程,根据 服务器性能和逻辑需求 改动
    private static Threads[] threads;

    /**
     * 解析协议,8-16个 浮动
     */
    private static ExecutorService executor;

    public static void init() {

        threads = new Threads[Const.Login + Const.Hall + Const.Match + Const.Game];
        // 给线程取名
        int temp = 0;
        int i = 0;
        for (; i < Const.Login; i++) {
            threads[i] = new Threads("Register_Login_" + temp++);
            threads[i].start();
        }
        temp = 0;
        for (; i < Const.Login + Const.Hall; i++) {
            threads[i] = new Threads("Hall_" + temp++);
            threads[i].start();
        }
        // 这里我们只定义了一个匹配线程,不需要循环了
        threads[i] = new Threads("Match_" + 0);
        threads[i].start();
        // 将匹配线程交给 匹配管理器
        MatchingManager.init(threads[i]);
        i++;

        temp = 0;
        for (; i < Const.Login + Const.Hall + Const.Match + Const.Game; i++) {
            threads[i] = new Threads("Game_" + temp++);
            threads[i].start();
        }

        //构造线程池
        executor = new ThreadPoolExecutor(8, 16,
                120L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());


        global.execute(() -> {
            long ms = System.currentTimeMillis();
            long lastUpdate = ms;
            long openBattle = ms;
            while (true) {
                try {
                    long nowMs = System.currentTimeMillis();
                    // 100 毫秒一次,循环全部房间
                    if (nowMs - lastUpdate > 100) {
                        //将匹配线程数据交给房间线程
                        MatchingManager.update();
                        lastUpdate = nowMs;
                    }

                    // 500毫米一次 检查是否可以开启对局
                    if (nowMs - openBattle > 500) {
                        MatchingManager.openBattle();
                        openBattle = nowMs;
                    }
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void pushRequest(ChannelHandlerContext ctx, byte[] msg) {
        executor.execute(() -> {
            executeRequest(ctx, msg);
        });
    }

    /**
     * 仅仅提供给 客户端请求 的处理
     *
     * @param ctx
     * @param msg
     */
    private static void executeRequest(ChannelHandlerContext ctx, byte[] msg) {
        // long millis = System.currentTimeMillis();

        // 读取前2位,拿到消息号
        byte[] codeByte = new byte[]{msg[0], msg[1]};
        int code = bytesToInt(codeByte);

        // 获取连接,如果没有连接; 说明是非法链接,直接拒绝
        Connection conn = getConn(ctx, code);
        if (conn == null) {
            ctx.close();
            return;
        }

        // 根据消息号确定用哪个类 来执行
        ATask task = TaskFactory.get(code);
        if (task == null) {
            LoggerManager.info("未知消息号: %s", code);
            return;
        }
        // LoggerManager.info("收到消息号: %s", code);
        // 将收到的数据 解析,并赋值给 task
        task.init(conn, msg);

        push(task);

        // LoggerManager.info("处理 %s 耗时 %s 毫秒", task.getCode(), System.currentTimeMillis() - millis);

    }

    public static void push(ITask task) {
        int threadIndex = task.getThreadIndex();
        threads[threadIndex].push(task);
    }

    /**
     * 除了注册/登录,其他的线程都在这里分配;
     *
     * @param type 操作类型
     * @param code 注册/登录,使用账号的hashcode; 对局使用 房间线程id; 其他地方使用uid
     * @return
     */
    public static int getThreadIndex(EThreadType type, int code) {
        switch (type) {
            case Login:
                return code % Const.Login;
            case Hall:
                int h = code % Const.Hall;
                return Const.Login + h;
            case Match:
                // 因为匹配线程 只有1条.这里不需要 取模
                // int m = player.uid % Const.Match;
                return Const.Login + Const.Hall;
            case Battle:
                return Const.Login + Const.Hall + Const.Match + code;

            default:
                return 0;
        }
    }

    /**
     * 获取一个 线程执行 任务
     *
     * @param type
     * @param code
     * @return
     */
    public static Threads getThreads(EThreadType type, int code) {
        int threadIndex = getThreadIndex(type, code);
        return threads[threadIndex];
    }


    public static void exit() {
        executor.shutdown();
        global.shutdown();
        try {
            for (Threads thread : threads) {
                thread.setExit();
                thread.join();
            }
        } catch (Exception e) {
            System.out.println("退出时,线程发生意外" + e);
        }
    }

    private static Connection getConn(ChannelHandlerContext ctx, int code) {
        Connection conn = ConnectionManager.getNetConn(ctx);
        if (conn == null && (code == ECode.Login.getCode() || code == ECode.Register.getCode())) {
            conn = new Connection(ctx);
            ConnectionManager.addNetConn(conn);
        }
        return conn;
    }

    // 大端，高字节在后
    private static int bytesToInt(byte[] bytes) {
        return (bytes[0] & 0xff) | ((bytes[1] & 0xff) << 8);
    }
}
