package com.st.modules.thread.framework.v4;


import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/** 内存实现：开发/单机环境使用。生产可替换为 Redis 版本（用 Pub/Sub 做广播） */
public final class InMemoryTicketStore implements TicketStore {

    private final ConcurrentHashMap<String, Ticket> tickets = new ConcurrentHashMap<String, Ticket>();
    private final ConcurrentHashMap<String, Set<Consumer<Ticket>>> listeners = new ConcurrentHashMap<String, Set<Consumer<Ticket>>>();

    public void saveTicket(Ticket t){ tickets.put(t.taskId, t); }
    public Ticket getTicket(String taskId){ return tickets.get(taskId); }

    public void publishUpdate(String taskId, Ticket t){
        Set<Consumer<Ticket>> set = listeners.get(taskId);
        if (set != null) {
            for (Consumer<Ticket> c : set) {
                try { c.accept(t); } catch (Throwable ignore) {}
            }
        }
    }

    public void subscribe(String taskId, Consumer<Ticket> listener){
        listeners.computeIfAbsent(taskId, new java.util.function.Function<String, Set<Consumer<Ticket>>>() {
            public Set<Consumer<Ticket>> apply(String k){
                return java.util.Collections.newSetFromMap(new ConcurrentHashMap<Consumer<Ticket>, Boolean>());
            }
        }).add(listener);
    }
    public void unsubscribe(String taskId, Consumer<Ticket> listener){
        Set<Consumer<Ticket>> set = listeners.get(taskId);
        if (set != null) set.remove(listener);
    }
}

