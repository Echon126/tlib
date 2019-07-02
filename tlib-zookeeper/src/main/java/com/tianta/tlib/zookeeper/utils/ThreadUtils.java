/**
 * 上海中赢金融信息服务有限公司
 * Copyright (c) 2017-2027 Chinazyjr,Inc.All Rights Reserved.
 */

package com.tianta.tlib.zookeeper.utils;

import com.google.common.base.Throwables;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * <b>ClassName:</b> ThreadUtils.java <br/>
 * <b>Description:</b> TODO <br/>
 * <b>Date:</b>     2016年4月15日 下午3:33:44<br/> 
 * @author   mobing
 * @version  	 
 */
public class ThreadUtils {
    private static final Logger log = LoggerFactory.getLogger(ThreadUtils.class);

    public static void checkInterrupted(Throwable e)
    {
        if ( e instanceof InterruptedException )
        {
            Thread.currentThread().interrupt();
        }
    }

    public static ExecutorService newSingleThreadExecutor(String processName)
    {
        return Executors.newSingleThreadExecutor(newThreadFactory(processName));
    }

    public static ExecutorService newFixedThreadPool(int qty, String processName)
    {
        return Executors.newFixedThreadPool(qty, newThreadFactory(processName));
    }

    public static ScheduledExecutorService newSingleThreadScheduledExecutor(String processName)
    {
        return Executors.newSingleThreadScheduledExecutor(newThreadFactory(processName));
    }

    public static ScheduledExecutorService newFixedThreadScheduledPool(int qty, String processName)
    {
        return Executors.newScheduledThreadPool(qty, newThreadFactory(processName));
    }

    public static ThreadFactory newThreadFactory(String processName)
    {
        return newGenericThreadFactory("Curator-" + processName);
    }

    public static ThreadFactory newGenericThreadFactory(String processName)
    {
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException(Thread t, Throwable e)
            {
                log.error("Unexpected exception in thread: " + t, e);
                Throwables.propagate(e);
            }
        };
        return new ThreadFactoryBuilder()
            .setNameFormat(processName + "-%d")
            .setDaemon(true)
            .setUncaughtExceptionHandler(uncaughtExceptionHandler)
            .build();
    }

    public static String getProcessName(Class<?> clazz)
    {
        if ( clazz.isAnonymousClass() )
        {
            return getProcessName(clazz.getEnclosingClass());
        }
        return clazz.getSimpleName();
    }
}

