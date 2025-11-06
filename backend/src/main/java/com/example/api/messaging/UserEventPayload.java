package com.example.api.messaging;

import java.time.OffsetDateTime;

public record UserEventPayload(
        String eventType,
        Long userId,
        String username,
        String email,
        String role,
        OffsetDateTime occurredAt
) {
}
