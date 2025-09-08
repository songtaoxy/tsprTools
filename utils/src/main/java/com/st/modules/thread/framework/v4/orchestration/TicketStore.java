package com.st.modules.thread.framework.v4.orchestration;


import java.util.function.Consumer;

/** 票据仓库：持久化 + 广播（SSE 用） */
public interface TicketStore {
    void saveTicket(Ticket t);
    Ticket getTicket(String taskId);

    /** 广播更新（SSE） */
    void publishUpdate(String taskId, Ticket t);

    /** 订阅/取消订阅某票据的更新（用于 SSE 推送） */
    void subscribe(String taskId, Consumer<Ticket> listener);
    void unsubscribe(String taskId, Consumer<Ticket> listener);
}
