package com.st.modules.thread.framework.v4;


import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.Callable;

/** 常用包装器：MDC + BizContext 捕获并恢复 */
public final class TaskWrappers {
    private TaskWrappers(){}


    /**
     * <pre>
     *     - 相关用法, 见{@link TaskWrapper#wrap(Callable) }
     * </pre>
     * @return
     */
    public static TaskWrapper bizAndMdcCapture() {
        return new TaskWrapper() {
            public Runnable wrap(final Runnable r) {
                final Map<String,String> parentMdc = MDC.getCopyOfContextMap();
                final BizContext parentCtx = BizContextHolder.get();
                return new Runnable() {
                    public void run() {
                        Map<String,String> old = MDC.getCopyOfContextMap();
                        if (parentMdc != null) MDC.setContextMap(parentMdc); else MDC.clear();
                        // try ( … ) { … } 是 try-with-resources 语法；块结束会自动调用 close()
                        // 核心是 try-with-resources + AutoCloseable Scope
                        try (BizContextHolder.Scope scope = BizContextHolder.with(parentCtx)) {
                            // 清除可能遗留的中断标志，避免跨任务泄漏
                            Thread.interrupted();
                            r.run();
                        } finally {
                            if (old != null) MDC.setContextMap(old); else MDC.clear();
                        }
                    }
                };
            }


            /**
             * 分析, ref。obsidian/企业高并发基座-V4-线程间上下文传递, 以及使用try with释放资源
             *
             * <pre>
             *     - “在提交时把主线程的日志上下文（MDC）+ 业务上下文（BizContext）抓拍下来；
             *     - 在工作线程里(新的线程中), 恢复这两份上下文，再执行真实任务；
             *     - 最后彻底还原/清理，防止线程池复用导致的串 context/串日志。”
             * </pre>
             *
             *
             * <pre>
             * 用法:
             * 每次起新线程时, 进行包装, 主要目的是:
             * 将主线程, 上游线程的信息, 传递到新起的线程中.
             *
             *
             * 相关逻辑
             * 这样修改之后，无论业务、超时、fallback、补全都能在正确的上下文中运行，
             * 且任务结束后上下文会被彻底还原，不会污染线程池里的下一个任务
             *
             * 业务线程：supply(...)
             *   ├─ wrapCallable(supplier)      ← 捕获父 MDC/CTX
             *   ├─ submit(callable)            → 工作线程执行（恢复 MDC/CTX）
             *   ├─ wrapRunnable(completer)     ← 捕获父 MDC/CTX
             *   ├─ executor.execute(completer) → 工作线程执行（恢复 MDC/CTX）
             *   └─ wrapRunnable(onTimeout)     ← 捕获父 MDC/CTX
             *       └─ scheduler.schedule(...) → 调度线程执行（恢复 MDC/CTX）
             *            └─ completeWithFallbackWrapped(...)（内部再 wrap fallback）
             * </pre>
             *
             * <pre>
             * 一句话总结:
             * - 捕获主线程上下文（MDC + BizContext）
             * - 恢复到工作线程 → 清中断 → 执行任务
             * - 恢复/清理工作线程原有上下文（MDC + BizContext）
             *
             *
             * 这样就解决了线程池复用下最常见的三类问题：
             * - 子线程日志没有 traceId/taskId；
             * - 上个任务的 MDC/BizContext 串到了下个任务；
             * - 上个任务留下的 interrupt 影响了下个任务。
             *
             * 备注:
             * 这里既在 wrapper 里 手动 set MDC，又在 BizContextHolder.with(...) 里可能再次 set MDC，
             * 虽然不冲突（后者会覆盖同名键），但从工程简洁性看，二选一即可：
             * - 要么让 BizContextHolder.with(...) 负责写 MDC；
             * - 要么保留你现在的 MDC set/clear，把 with(...) 只作为 ThreadLocal 的安装/恢复。
             *
             * </pre>
             * @param c
             * @return 返回一个包装后的 Runnable（Engine 在真正 execute() 前会用它包住原任务）
             * @param <T>
             */
            public <T> Callable<T> wrap(final Callable<T> c) {
                final Map<String,String> parentMdc = MDC.getCopyOfContextMap();
                final BizContext parentCtx = BizContextHolder.get();
                return new Callable<T>() {
                    public T call() throws Exception {
                        // 捕获父上下文
                        Map<String,String> old = MDC.getCopyOfContextMap();
                        if (parentMdc != null) MDC.setContextMap(parentMdc); else MDC.clear();
                        // try ( … ) { … } 是 try-with-resources 语法；
                        // 核心是 try-with-resources + AutoCloseable Scope
                        // 退出 try 块会调用 scope.close()，自动把 ThreadLocal/MDC 恢复成进入前的状态，防止泄漏到下一个任务
                        // try ( … ) { … } 是 try-with-resources 语法
                        // 1, 块结束会自动调用 close()
                        // 2, 块结束是指, “try ( … ) { … }”中的 “{...}" 执行完 -》
                        // 2.1 如果是当前线程, 即没有另起线程, 则顺序执行
                        // 2.2 如果新起线程, 如下面  c.call() -》 则等待当前任务完成.  ref obsidian/try with
                        try (BizContextHolder.Scope scope = BizContextHolder.with(parentCtx)) {
                            // 清掉可能残留的中断位 ref obsidian/interrupted (java Thread)
                            // 若上一任务曾设置过 interrupt，不清理的话当前任务里 sleep/阻塞 可能立刻抛 InterruptedException，导致“跨任务污染”。这行会读取并清除中断标志
                            Thread.interrupted();

                            // 执行真实任务
                            return c.call();
                        } finally {
                            // 恢复工作线程原有 MDC
                            // 确保无论任务是否异常、是否中途返回，MDC 都会回到任务执行前的状态。配合上面的 Scope.close()，双保险防止任何泄漏
                            if (old != null) MDC.setContextMap(old);
                            else MDC.clear();
                        }
                    }
                };
            }
        };
    }
}
