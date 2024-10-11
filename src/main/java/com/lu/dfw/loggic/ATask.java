package com.lu.dfw.loggic;

import com.lu.dfw.entity.Player;
import com.lu.dfw.manager.LoggerManager;
import com.lu.dfw.network.Connection;
import com.lu.dfw.network.ConnectionManager;
import com.lu.dfw.thread.ITask;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author
 * @Description 处理请求的 抽象类,所有处理请求对象的 总父类
 * @Version 1.0
 */
public abstract class ATask<T> implements ITask {
    private static Map<Class, Method> classMethodMap = new ConcurrentHashMap<>();

    // 玩家和客户端的连接
    protected Connection conn;
    // 本次收到的消息
    protected T request;
    protected Player player;

    /**（重点）
     * 根据 泛型 反射
     *
     * @param bytes
     * @return
     */
    private T msgToProto(byte[] bytes) {
        try {
            Class<T> clz = (Class<T>) ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            Method method = classMethodMap.get(clz);
            if(method == null){
                method = clz.getMethod("parseFrom", byte[].class);
                method.setAccessible(true);
                classMethodMap.put(clz, method);
            }
            byte[] newByte = new byte[bytes.length-2];
            System.arraycopy(bytes, 2, newByte, 0, newByte.length);
            return (T) method.invoke(clz, newByte);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 初始化 消息
     *
     * @param conn
     * @param msg
     */
    public void init(Connection conn, byte[] msg) {
        this.request = msgToProto(msg);
        this.player = conn.player;
        this.conn = conn;
    }

    public void execute() {
        long millis = System.currentTimeMillis();
        execute0();
        LoggerManager.info("处理 %s 耗时 %s 毫秒", getCode(), System.currentTimeMillis() - millis);
    }


    public abstract void execute0();

    public abstract ECode getCode();

    public void sendMsg(ECode code, byte[] msg) {
        conn.send(pack(code, msg));
    }

    protected void sendMsg(byte[] msg) {
        sendMsg(getCode(), msg);
    }

    public static void sendMsgToOne(int uid, ECode code, byte[] msg) {
        Connection connection = ConnectionManager.getNetConn(uid);
        if (connection != null) {
            byte[] pack = pack(code, msg);
            connection.send(pack);
        }
    }

    public static void sendMsgToList(Collection<Player> us, ECode code, byte[] msg) {
        for (Player u : us) {
            sendMsgToOne(u.uid, code, msg);
        }
    }

    public static void sendMsgToList(ECode code, byte[] msg, Player... us) {
        for (Player u : us) {
            sendMsgToOne(u.uid, code, msg);
        }
    }


    /**
     * task 自己选择 执行的线程id
     * 默认使用 登录线程
     * 允许 子类重写
     *
     * @return
     */
    public abstract int getThreadIndex();

    public static byte[] pack(ECode code, byte[] bytes) {
        int len = bytes.length;
        // 4个长度记录包长, 2个长度协议号 ,剩余为消息 数据
        byte[] pack = new byte[len + 4 + 2];

        // 包头长度
        byte[] packLen = intToByte4(bytes.length + 2);
        byte[] codeLen = intToByte2(code.getCode());

        System.arraycopy(packLen, 0, pack, 0, packLen.length);
        System.arraycopy(codeLen, 0, pack, packLen.length, codeLen.length);
        System.arraycopy(bytes, 0, pack, packLen.length + codeLen.length, bytes.length);
        return pack;
    }

    static byte[] intToByte4(int len) {
        byte[] len4 = new byte[4];
        len4[0] = (byte) (len & 0xff);
        len4[1] = (byte) (len >> 8 & 0xff);
        len4[2] = (byte) (len >> 16 & 0xff);
        len4[3] = (byte) (len >> 24 & 0xff);
        return len4;
    }

    /**
     * 将一个数字转为 byte数组; 1位byte最大可以表示255,2位最大表示65535,但是java有符号
     *
     * @param len
     * @return
     */
    static byte[] intToByte2(int len) {
        byte[] len2 = new byte[2];
        len2[0] = (byte) (len & 0xff);
        len2[1] = (byte) ((len & 0xff00) >> 8);
        return len2;
    }

}