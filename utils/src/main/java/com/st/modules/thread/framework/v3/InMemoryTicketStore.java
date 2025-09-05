package com.st.modules.thread.framework.v3;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

public final class InMemoryTicketStore implements TicketStore {
    private final ConcurrentMap<String, Object> map = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public void save(String taskId, Object value) { map.put(taskId, value); }
    public Object get(String taskId) { return map.get(taskId); }

    public void registerEmitter(String taskId, SseEmitter emitter) {
        emitters.computeIfAbsent(taskId, k -> new CopyOnWriteArrayList<>()).add(emitter);
        emitter.onCompletion(() -> emitters.getOrDefault(taskId, new CopyOnWriteArrayList<>()).remove(emitter));
        emitter.onTimeout(() -> emitters.getOrDefault(taskId, new CopyOnWriteArrayList<>()).remove(emitter));
    }

    public void publishUpdate(String taskId, Ticket t) {
        List<SseEmitter> list = emitters.get(taskId);
        if (list == null || list.isEmpty()) return;
        for (SseEmitter e : list) {
            try { e.send(SseEmitter.event().name("progress").data(t)); }
            catch (IOException ex) { e.complete(); }
        }
    }
}

