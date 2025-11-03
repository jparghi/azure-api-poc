package com.example.api.audit;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

@Service
public class AuditLogService {

    private static final int MAX_EVENTS = 1000;
    private final Deque<AuditLogEntry> events = new LinkedList<>();

    public synchronized void addEvent(String method, String path, String user, int status) {
        if (events.size() >= MAX_EVENTS) {
            events.removeFirst();
        }
        events.addLast(new AuditLogEntry(Instant.now(), method, path, user, status));
    }

    public synchronized List<AuditLogEntry> recentEvents() {
        return List.copyOf(events);
    }
}
