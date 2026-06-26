package com.awrs.api.dto;

import com.awrs.model.Item;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateItemRequest(
        @NotBlank String sku,
        @NotBlank String description,
        String supplier,
        String unitOfMeasure,
        @Min(0) int minimumThreshold,
        @Min(0) int reorderPoint
) {
    public Item toItem() {
        return new Item(sku, description, supplier, unitOfMeasure, minimumThreshold, reorderPoint);
    }
}
