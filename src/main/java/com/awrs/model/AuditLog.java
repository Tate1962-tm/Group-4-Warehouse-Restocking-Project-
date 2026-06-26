package com.awrs.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class AuditLog {

    public enum Action {
        RECEIVE,
        FULFILL,
        ADJUST,
        RESTOCK_COMPLETE
    }

    private final String id;
    private final Action action;
    private final String itemSku;
    private final String locationId;
    private final int quantityChange;
    private final String performedBy;
    private final String reason;
    private final Instant timestamp;

    public AuditLog(Action action, String itemSku, String locationId, int quantityChange,
                    String performedBy, String reason) {
        this(UUID.randomUUID().toString(), action, itemSku, locationId, quantityChange,
                performedBy, reason, Instant.now());
    }

    public AuditLog(String id, Action action, String itemSku, String locationId, int quantityChange,
                    String performedBy, String reason, Instant timestamp) {
        this.id = id;
        this.action = action;
        this.itemSku = itemSku;
        this.locationId = locationId;
        this.quantityChange = quantityChange;
        this.performedBy = performedBy;
        this.reason = reason;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public Action getAction() {
        return action;
    }

    public String getItemSku() {
        return itemSku;
    }

    public String getLocationId() {
        return locationId;
    }

    public int getQuantityChange() {
        return quantityChange;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public String getReason() {
        return reason;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuditLog auditLog = (AuditLog) o;
        return Objects.equals(id, auditLog.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
