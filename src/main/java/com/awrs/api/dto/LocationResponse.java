package com.awrs.api.dto;

import com.awrs.model.WarehouseLocation;

public record LocationResponse(
        String id,
        String name,
        String type,
        String fullPath
) {
    public static LocationResponse from(WarehouseLocation location) {
        return new LocationResponse(
                location.getId(),
                location.getName(),
                location.getType(),
                location.getFullPath());
    }
}
