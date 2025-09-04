package com.st.modules.thread.framework.v1;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.st.modules.thread.threadpool.CallableThreadPoolUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * <pre>
 *     - Orchestrator: 编排器; 在系统架构中，表示负责协调多个组件、任务或服务的执行顺序与依赖关系的模块
 * </pre>
 */
@Service
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrchestratorService {

    @Autowired
    private CoreService coreService;
    @Autowired
    private Dispatcher dispatcher;

    // 配置重试参数（可提取为配置类）
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000L;

    public void processTasks(List<String> names) {
        Executor executor = CallableThreadPoolUtils.getExecutor("dispatch-pool");

        // 所有异步任务的 Future
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // 所有最终结果 taskId -> 结果/失败信息
        Map<String, String> finalResultMap = new ConcurrentHashMap<>();

        String traceId = UUID.randomUUID().toString();

        for (String name : names) {
            String taskId = UUID.randomUUID().toString();
            ObjectNode ext = JsonNodeFactory.instance.objectNode();
            ext.put("taskId", taskId);
            ext.put("status", "INIT");
            ext.put("traceId", traceId);

            AsyncTaskContext ctx = new AsyncTaskContext(taskId, ext);

            // 构造每个异步任务
            CompletableFuture<Void> future = new CompletableFuture<>();

            dispatchWithRetry(taskId, ctx, name, 0, executor, finalResultMap, future);

            futures.add(future);
        }

        // 使用 allOf 统一收集任务完成后回调; 此步不阻塞主线程.
        CompletableFuture
                .allOf(futures.toArray(new CompletableFuture[0]))
                .thenRunAsync(() -> {
                    System.out.println(">>> 所有任务完成，按 taskId 输出结果：");
                    finalResultMap.forEach((k, v) -> System.out.println(k + " -> " + v));
                }, executor);

         // 使用 allOf 统一收集任务完成后回调; 此步阻塞主线程.
       /* CompletableFuture
                .allOf(futures.toArray(new CompletableFuture[0]))
                .thenRunAsync(() -> {
                    System.out.println(">>> 所有任务完成，按 taskId 输出结果：");
                    finalResultMap.forEach((k, v) -> System.out.println(k + " -> " + v));
                }, executor)
                .join();*/
    }

    /**
     * 带重试的任务分发（递归调度）
     */
    private void dispatchWithRetry(
            String taskId,
            AsyncTaskContext ctx,
            String name,
            int retryCount,
            Executor executor,
            Map<String, String> resultMap,
            CompletableFuture<Void> future
    ) {
        dispatcher.dispatch(ctx,
                () -> coreService.handleBusiness(taskId,name, ctx.getMetadata()),
                new Callback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        ctx.getMetadata().put("status", "SUCCESS");
                        ctx.complete(result);
                        resultMap.put(ctx.getTaskId(), result);
                        future.complete(null); // 标记任务完成
                    }

                    @Override
                    public void onFailure(Throwable ex) {
                        if (retryCount < MAX_RETRIES) {
                            ctx.getMetadata().put("status", "RETRYING_" + retryCount);
                            CompletableFuture.runAsync(() -> {
                                try {
                                    Thread.sleep(RETRY_DELAY_MS);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                                dispatchWithRetry(taskId,ctx, name, retryCount + 1, executor, resultMap, future);
                            }, executor);
                        } else {
                            ctx.getMetadata().put("status", "FAILED");
                            resultMap.put(ctx.getTaskId(), "失败: " + ex.getMessage());
                            future.complete(null); // 即使失败，也需标记完成，避免 allOf 卡住
                        }
                    }
                },
                executor // <--- 使用指定线程池调度任务
        );
    }
}
