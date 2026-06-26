package com.awrs.api.dto;

public record ErrorResponse(
        String error,
        String message
) {
}
