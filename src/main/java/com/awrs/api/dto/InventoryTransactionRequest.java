package com.awrs.api.dto;

import com.awrs.model.InventoryRecord;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record InventoryTransactionRequest(
        @NotBlank String itemSku,
        @NotBlank String locationId,
        @Min(1) int quantity
) {
}
