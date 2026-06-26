package com.awrs.api.dto;

import com.awrs.model.Role;
import com.awrs.model.User;

public record LoginResponse(
        String token,
        String username,
        Role role
) {
    public static LoginResponse from(String token, User user) {
        return new LoginResponse(token, user.getUsername(), user.getRole());
    }
}
