package com.awrs.api.dto;

import com.awrs.model.Role;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String username,
        @NotBlank String password
) {
}
