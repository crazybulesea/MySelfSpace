package com.lu.dfw.thread;

/**
 * @Author
 * @Data 2022/11/27 16:47
 * @Description
 * @Version 1.0
 */
public enum EThreadType {
    Login(1),          // 登录请求
    Hall(2),           // 大厅线程
    Match(3),           // 匹配专用线程
    Battle(4),           // 对局线程


    ;
    private int code;

    EThreadType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static EThreadType getType(int code) {
        EThreadType[] values = values();
        for (int i = 0; i < values.length; i++) {
            if (code == values[i].code) {
                return values[i];
            }
        }
        return Login;
    }


}
