package com.st.modules.thread.framework.v3;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/** 异步任务上下文（任务单） */
public final class AsyncTaskContext {
    public enum Status { INIT, RUNNING, SUCCESS, FAILED }

    private final String taskId = UUID.randomUUID().toString();
    private final ObjectNode metadata;
    private final Map<String,Object> results = new ConcurrentHashMap<String,Object>();
    private final AtomicReference<Status> status = new AtomicReference<Status>(Status.INIT);

    public AsyncTaskContext(ObjectNode metadata) { this.metadata = metadata; }

    public String taskId() { return taskId; }
    public ObjectNode meta() { return metadata; }
    public Map<String,Object> results() { return results; }
    public Status status() { return status.get(); }

    public void running() { status.set(Status.RUNNING); if (metadata!=null) metadata.put("status","RUNNING"); }
    public void success(Object v) { status.set(Status.SUCCESS); if (metadata!=null) metadata.put("status","SUCCESS"); results.put(taskId, v); }
    public void fail(Throwable ex) {
        status.set(Status.FAILED);
        if (metadata!=null) { metadata.put("status","FAILED"); if (ex!=null) metadata.put("error", String.valueOf(ex.getMessage())); }
    }
}

