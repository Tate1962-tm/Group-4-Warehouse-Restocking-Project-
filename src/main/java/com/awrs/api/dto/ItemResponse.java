package com.awrs.api.dto;

import com.awrs.model.Item;

public record ItemResponse(
        String sku,
        String description,
        String supplier,
        String unitOfMeasure,
        int minimumThreshold,
        int reorderPoint
) {
    public static ItemResponse from(Item item) {
        return new ItemResponse(
                item.getSku(),
                item.getDescription(),
                item.getSupplier(),
                item.getUnitOfMeasure(),
                item.getMinimumThreshold(),
                item.getReorderPoint());
    }
}
