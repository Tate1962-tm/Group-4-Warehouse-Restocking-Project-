package com.awrs.api.dto;

import com.awrs.model.Role;
import com.awrs.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
        @NotBlank String username,
        @NotBlank String password,
        @NotNull Role role
) {
    public User toUser() {
        return new User(username, password, role);
    }
}
