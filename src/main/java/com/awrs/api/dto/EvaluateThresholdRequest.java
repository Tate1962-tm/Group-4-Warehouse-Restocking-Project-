package com.awrs.api.dto;

import jakarta.validation.constraints.NotBlank;

public record EvaluateThresholdRequest(
        @NotBlank String itemSku,
        @NotBlank String locationId
) {
}
