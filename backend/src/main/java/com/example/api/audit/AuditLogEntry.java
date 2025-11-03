package com.example.api.audit;

import java.time.Instant;

public record AuditLogEntry(Instant timestamp, String method, String path, String user, int status) {
}
