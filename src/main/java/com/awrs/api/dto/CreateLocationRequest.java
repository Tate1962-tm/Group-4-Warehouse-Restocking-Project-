package com.awrs.api.dto;

import com.awrs.model.WarehouseLocation;
import jakarta.validation.constraints.NotBlank;

public record CreateLocationRequest(
        @NotBlank String id,
        @NotBlank String name,
        String type,
        String parentId
) {
}
