package com.tianta.tlib.redis;

/**
 * 西安中科天塔科技股份有限公司
 * Copyright (c) 2019-2029, tianta All Rights Reserved
 *
 * @version 1.0
 * @author: yf
 * @create: 2019/7/2
 */
public class RedisExceptionUtil {

    private RedisExceptionUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * @param keys 检测keys值为空的情况。keys为不定长参数
     */
    public static void checkKeyException(String... keys) {
        for (String key : keys) {
            //为空抛出异常
            if (key == null || key.equals("")) {
                throw new NullPointerException("redis->key不能为空 ");
            }
        }
    }

    /**
     * 检测传入对象是否为null
     *
     * @param obj
     */
    public static void checkObjectIsNull(Object obj) {
        if (obj == null) {
            throw new NullPointerException("对象不能为空 ");
        }
    }

    /**
     * 检测传入对象是否为null
     *
     * @param obj
     */
    public static void checkObjectIsNull(Object obj, String reason) {
        if (obj == null) {
            throw new NullPointerException(reason);
        }
    }
}
