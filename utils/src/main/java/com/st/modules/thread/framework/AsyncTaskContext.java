package com.st.modules.thread.framework;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.Generated;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <pre>
 *
 * </pre>
 */
@Data
@Setter
@Generated
public class AsyncTaskContext {
    private final String taskId;
    private final ObjectNode metadata;
    private final Map<String, Object> resultMap = new ConcurrentHashMap<>();
    private final AtomicReference<TaskStatus> status = new AtomicReference<>(TaskStatus.INIT);

    public AsyncTaskContext(String taskId, ObjectNode metadata) {
        this.taskId = taskId;
        this.metadata = metadata;
    }

    public void complete(Object result) {
        resultMap.put(taskId, result);
        status.set(TaskStatus.SUCCESS);
    }

    public void fail(Throwable ex) {
        status.set(TaskStatus.FAILED);
        metadata.put("error", ex.getMessage());
    }

    public enum TaskStatus { INIT, RUNNING, SUCCESS, FAILED }
}
