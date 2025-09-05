package com.st.modules.thread.framework.v3;


import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <pre>
 *     `Ticket` 就是**“任务单/票据”模型**：
 *     当 Controller 决定“先返回票据、后台继续跑”时，
 *     用它把**这次并发聚合任务**的全量状态装起来（任务 id、整体状态/进度、每个子任务的状态与错误、最终结果等），
 *     并持久到 `TicketStore`（内存或 Redis）。前端可通过 `/api/task/{id}` 或 SSE 实时看到它的变化
 * </pre>
 *
 * <pre>
 * 它在流程中的位置:
 * 创建：new Ticket(UUID..., names, strictAll) → status=PROCESSING 后 ticketStore.saveTicket(t)。
 *
 * 更新：每个子任务开始/完成/降级时，更新对应 Subtask 的 status/progress/error，调用 recomputeAggregates()，然后 publishUpdate(...) 触发 SSE 推送。
 *
 * 完成：全部子任务结束后，填 data（例如 List<ResultItem>），progress=100，status=SUCCESS（有损模式即使有 FALLBACK 也记 SUCCESS）。
 *
 * 查询：GET /api/task/{id} 直接从 TicketStore 取回这个对象返回给前端
 * </pre>
 */
public final class Ticket implements Serializable {
    public enum Status { INIT, PROCESSING, SUCCESS, FAILED }
    /** 阶段划分用于进度展示（可按需调整权重） */
    public enum Stage { PREPARE(10), CALL_DOWNSTREAM(60), MERGE(20), FINISH(10);
        public final int weight; Stage(int w){ this.weight = w; } }

    /** 子任务运行明细 */
    public static final class Subtask implements Serializable {
        public final String name;
        public volatile String status;    // INIT/RUNNING/SUCCESS/FALLBACK/FAILED
        public volatile int progress;     // 0..100
        public volatile String error;     // 失败/降级原因
        public volatile Stage stage = Stage.PREPARE;
        public final long startedAt = System.currentTimeMillis();
        public volatile long endedAt = 0L;
        public Subtask(String name){ this.name = name; this.status = "INIT"; this.progress = 0; }
    }

    /** 票据基础信息 */
    public final String taskId;
    public volatile String mode = "ASYNC";          // SYNC / ASYNC（是否同步返回）
    public volatile Status status = Status.INIT;    // 整体状态
    public volatile int progress = 0;               // 汇总进度 0..100
    public final Map<String, Subtask> subtasks = new ConcurrentHashMap<>(); // name -> 明细
    public volatile Object data;                    // 最终结果（如 List<ResultItem>）
    public volatile String error;                   // 整体失败原因（若 FAILED）

    public final long startedAt = System.currentTimeMillis();
    public volatile long updatedAt = System.currentTimeMillis();
    public volatile boolean strictAll = false;      // 是否“任一失败就整体失败”的严格模式标记

    /** 聚合统计，便于前端展示与告警 */
    public volatile int okCount = 0;
    public volatile int fallbackCount = 0;
    public volatile int failedCount = 0;
    public volatile int totalCount = 0;

    public Ticket(String taskId, List<String> names, boolean strictAll) {
        this.taskId = taskId; this.strictAll = strictAll;
        for (String n : names) subtasks.put(n, new Subtask(n));
        this.totalCount = names.size();
    }

    public void touch(){ this.updatedAt = System.currentTimeMillis(); }

    /** 按阶段权重更新子任务进度 */
    // Ticket.java
    public static void advanceStage(Subtask s, Stage next){
        int acc;
        switch (next) {
            case PREPARE:
                acc = Stage.PREPARE.weight;
                break;
            case CALL_DOWNSTREAM:
                acc = Stage.PREPARE.weight + Stage.CALL_DOWNSTREAM.weight;
                break;
            case MERGE:
                acc = Stage.PREPARE.weight + Stage.CALL_DOWNSTREAM.weight + Stage.MERGE.weight;
                break;
            case FINISH:
            default:
                acc = 100;
                break;
        }
        s.stage = next;
        s.progress = Math.min(100, acc);
    }


    /** 重算整体进度与统计（每次子任务落定后调用） */
    // Ticket.java
    public void recomputeAggregates(){
        int sum = 0, ok = 0, fb = 0, fail = 0, total = subtasks.size();
        for (Subtask s : subtasks.values()) {
            sum += s.progress; // 逐步推进的细粒度进度
            if ("SUCCESS".equals(s.status)) ok++;
            else if ("FALLBACK".equals(s.status)) fb++;
            else if ("FAILED".equals(s.status)) fail++;
        }
        this.progress = total == 0 ? 0 : Math.min(100, sum / total);
        this.okCount = ok;
        this.fallbackCount = fb;
        this.failedCount = fail;
        this.totalCount = total;
        touch();
    }

}

