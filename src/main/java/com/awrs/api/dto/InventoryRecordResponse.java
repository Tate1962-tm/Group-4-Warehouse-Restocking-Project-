package com.awrs.api.dto;

import com.awrs.model.InventoryRecord;

public record InventoryRecordResponse(
        String itemSku,
        String locationId,
        int quantity
) {
    public static InventoryRecordResponse from(InventoryRecord record) {
        return new InventoryRecordResponse(record.getItemSku(), record.getLocationId(), record.getQuantity());
    }
}
