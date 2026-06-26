package com.awrs.api.dto;

import com.awrs.model.AuditLog;

import java.time.Instant;

public record AuditLogResponse(
        String id,
        String action,
        String itemSku,
        String locationId,
        int quantityChange,
        String performedBy,
        String reason,
        Instant timestamp
) {
    public static AuditLogResponse from(AuditLog log) {
        return new AuditLogResponse(
                log.getId(),
                log.getAction().name(),
                log.getItemSku(),
                log.getLocationId(),
                log.getQuantityChange(),
                log.getPerformedBy(),
                log.getReason(),
                log.getTimestamp());
    }
}
