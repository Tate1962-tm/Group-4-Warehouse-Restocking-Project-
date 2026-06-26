package com.awrs.model;

import java.util.Objects;
import java.util.UUID;

public class RestockTask {

    private final String id;
    private final String itemSku;
    private final String locationId;
    private final int requestedQuantity;
    private final int priority;
    private RestockTaskStatus status;

    public RestockTask(String itemSku, String locationId, int requestedQuantity, int priority) {
        this(UUID.randomUUID().toString(), itemSku, locationId, requestedQuantity, priority, RestockTaskStatus.CREATED);
    }

    public RestockTask(String id, String itemSku, String locationId, int requestedQuantity,
                       int priority, RestockTaskStatus status) {
        if (itemSku == null || itemSku.isBlank()) {
            throw new IllegalArgumentException("Item SKU is required");
        }
        if (locationId == null || locationId.isBlank()) {
            throw new IllegalArgumentException("Location id is required");
        }
        if (requestedQuantity <= 0) {
            throw new IllegalArgumentException("Requested quantity must be positive");
        }
        this.id = id;
        this.itemSku = itemSku;
        this.locationId = locationId;
        this.requestedQuantity = requestedQuantity;
        this.priority = priority;
        this.status = status == null ? RestockTaskStatus.CREATED : status;
    }

    public String getId() {
        return id;
    }

    public String getItemSku() {
        return itemSku;
    }

    public String getLocationId() {
        return locationId;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public int getPriority() {
        return priority;
    }

    public RestockTaskStatus getStatus() {
        return status;
    }

    public void setStatus(RestockTaskStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RestockTask that = (RestockTask) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
