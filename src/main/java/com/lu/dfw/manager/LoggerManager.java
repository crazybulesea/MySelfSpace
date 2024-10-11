package com.lu.dfw.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author qzj
 * @Description
 * @Version 1.0
 */
public class LoggerManager {
    // 线程栈计数
    private static final int num = 4;

    private static Logger log = LoggerFactory.getLogger(LoggerManager.class);

    public static void info(String format, Object... args) {
        info0(String.format(format, args), num);
    }

    public static void error(String format, Object... args) {
        error0(String.format(format, args), num);
    }

    private static void info0(String format, int level) {
        log.info(String.format("%s%s", whoCalledMe(level), format));
    }

    private static void error0(String format, int level) {
        log.error(String.format("%s%s", whoCalledMe(level), format));
    }


    private static String whoCalledMe(int traceLevel) {
        if (traceLevel == -1) return "";
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stackTraceElements[traceLevel];
        String filename = caller.getFileName().replace(".java", "");
        int lineNumber = caller.getLineNumber();
        return "[" + filename + ":" + lineNumber + "] ";
    }

}
