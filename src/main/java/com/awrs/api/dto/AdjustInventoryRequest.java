package com.awrs.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AdjustInventoryRequest(
        @NotBlank String itemSku,
        @NotBlank String locationId,
        @Min(0) int newQuantity,
        @NotBlank String reason
) {
}
