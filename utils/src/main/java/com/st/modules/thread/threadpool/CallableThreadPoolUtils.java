package com.st.modules.thread.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通用 Callable 线程池工具类
 * <p>
 * <pre>
 * - 支持命名线程池复用
 * - 支持不带超时限制的提交方法, 完全复用现有线程池机制
 * - 支持任务超时控制
 * - 支持 invokeAll 批量提交
 * - 使用默认线程池参数; 同时支持自定义线程池参数（核心线程数、最大线程数、队列大小、存活时间）
 * - 只要前缀prefi相同, 则得到的线程池是同一个线程池; 因此, 同一个模块, 前缀一致, 好跟踪
 * </pre>
 *
 * <p></p>
 *  特性总结
 * <pre>
 * <b><u>支持返回结果</u></b>：相比 Runnable，可获取结果或处理异常
 * <b><u>线程命名唯一</u></b>：支持不同业务前缀，线程日志清晰可追踪
 * <b><u>线程池复用</u></b>：同一 prefix 多次获取返回同一线程池
 * <b><u>默认拒绝策略</u></b>：CallerRunsPolicy 避免任务丢失
 * </pre>
 *
 *  <p></p>
 *  Usage: 非控制(单个任务, 批量提交)
 *  <pre>
 *  {@code
 *      public static void main(String[] args) throws Exception {
 *         ThreadPoolExecutor executor = CallableThreadPoolUtils.getExecutor("analysis");
 *
 *         // 单个任务提交（无超时）
 *         Future<String> future = CallableThreadPoolUtils.submit(executor, () -> {
 *             String threadName = Thread.currentThread().getName();
 *             return threadName + " 执行单个任务（无超时）";
 *         });
 *         //13:30:01.224 [main] INFO com.st.modules.thread.threadpool.CallableThreadPoolUtils - analysis-thread-0 执行单个任务（无超时）
 *         log.info(future.get());
 *
 *         // 批量任务提交（无超时）
 *         List<Callable<String>> tasks = new ArrayList<Callable<String>>();
 *         for (int i = 0; i < 3; i++) {
 *             final int id = i;
 *             tasks.add(() -> {
 *                 String threadName = Thread.currentThread().getName();
 *                 return threadName + " 执行任务-" + id + "（无超时）";
 *             });
 *         }
 *
 *         List<Future<String>> results = CallableThreadPoolUtils.submitBatchWithoutTimeout(executor, tasks);
 *         // 13:30:01.226 [main] INFO com.st.modules.thread.threadpool.CallableThreadPoolUtils - analysis-thread-1 执行任务-0（无超时）
 *         //13:30:01.226 [main] INFO com.st.modules.thread.threadpool.CallableThreadPoolUtils - analysis-thread-2 执行任务-1（无超时）
 *         //13:30:01.226 [main] INFO com.st.modules.thread.threadpool.CallableThreadPoolUtils - analysis-thread-3 执行任务-2（无超时）
 *         for (Future<String> f : results) {
 *             log.info(f.get());
 *         }
 *
 *         executor.shutdown();
 *     }
 *  }
 *  </pre>
 *
 * <p></p>
 * Usage: 超时控制(单个任务, 批量提交)
 * <pre>
 * {@code
 *     // case: 超时控制(单个任务, 批量提交)
 *     public static void main(String[] args) throws Exception {
 *         ThreadPoolExecutor executor = CallableThreadPoolUtils.getExecutor("report");
 *
 *         // 单任务，带超时控制
 *         String result = CallableThreadPoolUtils.submitWithTimeout(executor, () -> {
 *             String threadName = Thread.currentThread().getName();
 *             return threadName + " 执行单任务完成";
 *         }, 1000);
 *         // 13:18:43.773 [main] INFO com.st.modules.thread.threadpool.CallableThreadPoolUtils - report-thread-0 执行单任务完成
 *         log.info(result); // 示例输出：report-thread-0 执行单任务完成
 *
 *         // 批量任务
 *         List<Callable<String>> taskList = new ArrayList<Callable<String>>();
 *         for (int i = 0; i < 3; i++) {
 *             int id = i;
 *             taskList.add(() -> {
 *                 String threadName = Thread.currentThread().getName();
 *                 return threadName + " 执行任务-" + id;
 *             });
 *         }
 *         List<Future<String>> futures = CallableThreadPoolUtils.submitBatch(executor, taskList, 2000);
 *         // 13:18:43.773 [main] INFO com.st.modules.thread.threadpool.CallableThreadPoolUtils - report-thread-0 执行单任务完成
 *         //13:18:43.775 [main] INFO com.st.modules.thread.threadpool.CallableThreadPoolUtils - report-thread-1 执行任务-0
 *         //13:18:43.775 [main] INFO com.st.modules.thread.threadpool.CallableThreadPoolUtils - report-thread-2 执行任务-1
 *         //13:18:43.775 [main] INFO com.st.modules.thread.threadpool.CallableThreadPoolUtils - report-thread-3 执行任务-2
 *         for (Future<String> future : futures) {
 *             log.info(future.get());
 *         }
 *
 *         executor.shutdown();
 *     }
 * }
 * </pre>
 *
 *
 * <p></p>
 * 待扩展
 * <pre>
 * - 每个 prefix 的参数支持配置（如读取 application.yaml）
 * 支持监控注册（如 Micrometer、Prometheus）
 * </pre>
 */
@Slf4j
public class CallableThreadPoolUtils {

    private static final int DEFAULT_CORE = 4;
    private static final int DEFAULT_MAX = 10;
    private static final int DEFAULT_QUEUE = 100;
    private static final long DEFAULT_KEEPALIVE = 60L;

    private static final ConcurrentHashMap<String, ThreadPoolExecutor> EXECUTOR_MAP = new ConcurrentHashMap<String, ThreadPoolExecutor>();

    private CallableThreadPoolUtils() {
        throw new UnsupportedOperationException("工具类禁止实例化");
    }

    /**
     * 获取指定前缀线程池，使用默认配置
     */
    public static ThreadPoolExecutor getExecutor(String prefix) {
        return getExecutor(prefix, DEFAULT_CORE, DEFAULT_MAX, DEFAULT_QUEUE, DEFAULT_KEEPALIVE);
    }

    /**
     * 获取自定义线程池
     *
     * @param prefix        线程名前缀
     * @param corePoolSize  核心线程数
     * @param maxPoolSize   最大线程数
     * @param queueSize     队列大小
     * @param keepAliveSecs 空闲线程保留时间（秒）
     */
    public static ThreadPoolExecutor getExecutor(String prefix, int corePoolSize, int maxPoolSize, int queueSize, long keepAliveSecs) {
        return EXECUTOR_MAP.computeIfAbsent(prefix, p -> new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveSecs,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(queueSize),
                new NamedThreadFactory(p),
                new ThreadPoolExecutor.CallerRunsPolicy()
        ));
    }


    /**
     * 提交单个 Callable 任务（无超时控制）
     *
     * @param executor 线程池
     * @param task     可执行任务
     * @param <T>      返回类型
     * @return Future 结果
     */
    public static <T> Future<T> submit(ExecutorService executor, Callable<T> task) {
        return executor.submit(task);
    }

    /**
     * 批量提交 Callable 任务（无超时控制）
     *
     * @param executor 线程池
     * @param tasks    任务列表
     * @param <T>      返回类型
     * @return 执行结果列表
     * @throws InterruptedException 被中断
     */
    public static <T> List<Future<T>> submitBatchWithoutTimeout(ExecutorService executor, List<Callable<T>> tasks)
            throws InterruptedException {
        return executor.invokeAll(tasks);
    }




    /**
     * 提交 Callable 任务，支持超时取消
     *
     * @param executor 执行器
     * @param task     任务
     * @param timeout  超时时间（毫秒）
     * @param <T>      返回类型
     */
    public static <T> T submitWithTimeout(ThreadPoolExecutor executor, Callable<T> task, long timeout) throws Exception {
        Future<T> future = executor.submit(task);
        try {
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw new TimeoutException("任务超时被取消");
        }
    }

    /**
     * 提交任务列表（批量），并等待所有结果返回/支持超时取消
     *
     * @param executor 执行器
     * @param tasks    任务列表
     * @param timeout  最大等待时长（毫秒）
     */
    public static <T> List<Future<T>> submitBatch(ThreadPoolExecutor executor, List<Callable<T>> tasks, long timeout) throws InterruptedException {
        return executor.invokeAll(tasks, timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * 命名线程工厂：prefix-thread-0, prefix-thread-1, ...
     */
    private static class NamedThreadFactory implements ThreadFactory {
        private final String prefix;
        private final AtomicInteger count = new AtomicInteger(0);

        public NamedThreadFactory(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName(prefix + "-thread-" + count.getAndIncrement());
            return t;
        }
    }






}
