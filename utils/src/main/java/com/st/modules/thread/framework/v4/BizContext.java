package com.st.modules.thread.framework.v4;


import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 业务上下文：Controller → 所有子线程贯穿，可并发读写
 */
public final class BizContext implements Serializable {

    /**
     * 可选的阶段记录（进度与状态可视化）
     */
    public static final class Stage implements Serializable {
        public final String name;
        public final AtomicInteger progress = new AtomicInteger(0); // 0..100
        public volatile String status = "INIT"; // INIT/RUNNING/SUCCESS/FAILED
        public volatile String note;            // 备注/失败原因
        public volatile long startedAt = 0L, endedAt = 0L;

        Stage(String name) {
            this.name = name;
        }

        public Stage start() {
            status = "RUNNING";
            startedAt = System.currentTimeMillis();
            return this;
        }

        public Stage ok() {
            status = "SUCCESS";
            progress.set(100);
            endedAt = System.currentTimeMillis();
            return this;
        }

        public Stage fail(String n) {
            status = "FAILED";
            note = n;
            endedAt = System.currentTimeMillis();
            return this;
        }

        public Stage setProgress(int p) {
            progress.set(Math.max(0, Math.min(100, p)));
            return this;
        }
    }

    // 基本标识
    public final String taskId, traceId, tenantId, userId;

    // 业务属性 & 阶段
    private final ConcurrentHashMap<String, Object> attrs = new ConcurrentHashMap<String, Object>();
    private final ConcurrentHashMap<String, Stage> stages = new ConcurrentHashMap<String, Stage>();

    // 元信息
    public final long createdAt = System.currentTimeMillis();
    private final AtomicLong lastUpdatedAt = new AtomicLong(createdAt);
    private final java.util.concurrent.ConcurrentMap<String, SubtaskContext> subtasks =
            new java.util.concurrent.ConcurrentHashMap<>();


    public BizContext(String taskId, String traceId, String tenantId, String userId) {
        this.taskId = taskId;
        this.traceId = traceId;
        this.tenantId = tenantId;
        this.userId = userId;
    }

    public BizContext put(String k, Object v) {
        attrs.put(k, v);
        touch();
        return this;
    }

    public Object get(String k) {
        return attrs.get(k);
    }

    public <T> T get(String k, Class<T> t) {
        Object v = attrs.get(k);
        return t.isInstance(v) ? t.cast(v) : null;
    }

    public Map<String, Object> view() {
        return java.util.Collections.unmodifiableMap(attrs);
    }

    public Stage stage(String name) {
        return stages.computeIfAbsent(name, new java.util.function.Function<String, Stage>() {
            public Stage apply(String n) {
                return new Stage(n);
            }
        });
    }

    public Map<String, Stage> stageView() {
        return java.util.Collections.unmodifiableMap(stages);
    }

    public long lastUpdatedAt() {
        return lastUpdatedAt.get();
    }

    public Instant lastUpdatedInstant() {
        return Instant.ofEpochMilli(lastUpdatedAt.get());
    }

    private void touch() {
        lastUpdatedAt.set(System.currentTimeMillis());
    }


    /**
     * 取/建某个子任务的上下文（同名同份；不同名彼此隔离）
     */
    public SubtaskContext sub(String name) {
        return subtasks.computeIfAbsent(name, SubtaskContext::new);
    }

    /**
     * 可选：快照查看
     */
    public java.util.Map<String, java.util.Map<String, Object>> viewSubs() {
        java.util.Map<String, java.util.Map<String, Object>> m = new java.util.HashMap<>();
        subtasks.forEach((k, v) -> m.put(k, v.view()));
        return java.util.Collections.unmodifiableMap(m);
    }

    /**
     * 每个子任务的隔离上下文
     */
    public static final class SubtaskContext {
        public final String name;
        private final java.util.concurrent.ConcurrentMap<String, Object> attrs = new java.util.concurrent.ConcurrentHashMap<>();
        // 常用字段可以提供专用原子引用，读写更顺手
        private final java.util.concurrent.atomic.AtomicReference<String> path = new java.util.concurrent.atomic.AtomicReference<>();

        SubtaskContext(String name) {
            this.name = name;
        }

        // 通用 KV
        public SubtaskContext put(String key, Object val) {
            if (val == null) attrs.remove(key);
            else attrs.put(key, val);
            return this;
        }

        @SuppressWarnings("unchecked")
        public <T> T get(String key) {
            return (T) attrs.get(key);
        }

        public java.util.Map<String, Object> view() {
            return java.util.Collections.unmodifiableMap(attrs);
        }

        // 针对“路径”的便捷 API
        public SubtaskContext setPath(String p) {
            path.set(p);
            attrs.put("path", p);
            return this;
        }

        public String getPath() {
            return path.get();
        }

        public SubtaskContext updatePath(java.util.function.UnaryOperator<String> fn) {
            String next = fn.apply(path.get());
            path.set(next);
            attrs.put("path", next);
            return this;
        }
    }


}

