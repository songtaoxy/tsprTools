package com.st.modules.thread.pool;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <li> 用法, see obsidian/thread </li>
 */
public class ThreadPoolUtil {

    public static ThreadPoolExecutor networkJobThreadPool = new ThreadPoolExecutor(5,
            10,
            4,
            TimeUnit.MINUTES,
            new LinkedBlockingDeque<>(50),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy());

    public static ThreadPoolExecutor corpMsgThreadPool = new ThreadPoolExecutor(1,
            2,
            4,
            TimeUnit.MINUTES,
            new LinkedBlockingDeque<>(50),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardPolicy());

    public static ThreadPoolExecutor emailThreadPool = new ThreadPoolExecutor(2,
            3,
            4,
            TimeUnit.MINUTES,
            new LinkedBlockingDeque<>(200),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardPolicy());


    public static ThreadPoolExecutor ccdJobThreadPool = new ThreadPoolExecutor(1,
            10,
            4,
            TimeUnit.MINUTES,
            new LinkedBlockingDeque<>(50),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardPolicy());

    public static ThreadPoolExecutor pullconfigThreadPool = new ThreadPoolExecutor(1,
            10,
            4,
            TimeUnit.MINUTES,
            new LinkedBlockingDeque<>(50),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardPolicy());

}