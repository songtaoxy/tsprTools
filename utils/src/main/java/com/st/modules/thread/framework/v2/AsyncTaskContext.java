package com.st.modules.thread.framework.v2;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 概述：异步任务上下文，承载taskId、元信息、结果集与状态
 * 功能清单：
 * 1）保存元数据与可扩展结果映射（以taskId为key）
 * 2）状态流转 INIT→RUNNING→SUCCESS/FAILED
 * 3）错误记录
 * 使用示例：AsyncTaskContext ctx = AsyncTaskContext.newContext(meta)
 * 注意事项：线程安全；结果对象建议不可变
 * 入参与出参与异常说明：入参为元数据；方法不抛检查异常
 */
public class AsyncTaskContext {
    public enum TaskStatus { INIT, RUNNING, SUCCESS, FAILED }

    private final String taskId;
    private final ObjectNode metadata;
    private final Map<String, Object> resultMap = new ConcurrentHashMap<String, Object>();
    private final AtomicReference<TaskStatus> status = new AtomicReference<TaskStatus>(TaskStatus.INIT);

    private AsyncTaskContext(String taskId, ObjectNode metadata) {
        this.taskId = taskId;
        this.metadata = metadata;
    }

    public static AsyncTaskContext newContext(ObjectNode metadata) {
        return new AsyncTaskContext(UUID.randomUUID().toString(), metadata);
    }

    public String getTaskId() { return taskId; }
    public ObjectNode getMetadata() { return metadata; }
    public Map<String, Object> getResultMap() { return resultMap; }
    public TaskStatus getStatus() { return status.get(); }

    public void running() { status.set(TaskStatus.RUNNING); }
    public void complete(Object result) {
        resultMap.put(taskId, result);
        status.set(TaskStatus.SUCCESS);
        metadata.put("status", "SUCCESS");
    }
    public void fail(Throwable ex) {
        status.set(TaskStatus.FAILED);
        if (ex != null) metadata.put("error", String.valueOf(ex.getMessage()));
        metadata.put("status", "FAILED");
    }
}
