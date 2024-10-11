package com.lu.dfw.utils;

import java.util.Random;

/**
 * @Author qzj
 * @Data 2023/9/21 10:00
 * @Description 随机数
 * @Version 1.0
 */
public class RandomUtil {
    static Random random = new Random();

    /**
     * 返回一个随机数,取值在0-num之间,不包含num
     *
     * @param num 0 - (num-1);
     * @return
     */
    public static int getRandom(int num) {
        return random.nextInt(num);
    }

    public static int getRoll() {
        return getRandom(2, 12);
    }

    /**
     * 随机一个区间; 最小值为 min,最大值为max;
     *
     * @param min
     * @param max
     * @return
     */
    public static int getRandom(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
}
