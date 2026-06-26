package com.awrs.api.dto;

import com.awrs.model.RestockTask;
import com.awrs.model.RestockTaskStatus;

public record RestockTaskResponse(
        String id,
        String itemSku,
        String locationId,
        int requestedQuantity,
        int priority,
        RestockTaskStatus status
) {
    public static RestockTaskResponse from(RestockTask task) {
        return new RestockTaskResponse(
                task.getId(),
                task.getItemSku(),
                task.getLocationId(),
                task.getRequestedQuantity(),
                task.getPriority(),
                task.getStatus());
    }
}
