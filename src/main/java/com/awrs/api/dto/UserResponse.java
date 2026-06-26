package com.awrs.api.dto;

import com.awrs.model.Role;
import com.awrs.model.User;

public record UserResponse(
        String username,
        Role role
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getUsername(), user.getRole());
    }
}
