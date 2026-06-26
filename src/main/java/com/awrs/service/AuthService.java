package com.awrs.service;

import com.awrs.exception.AuthenticationException;
import com.awrs.exception.DuplicateResourceException;
import com.awrs.exception.ForbiddenException;
import com.awrs.model.Role;
import com.awrs.model.User;
import com.awrs.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Username and password are required");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("Invalid username or password"));

        if (!user.authenticate(password)) {
            throw new AuthenticationException("Invalid username or password");
        }

        return user;
    }

    public User createUser(User admin, User newUser) {
        requireRole(admin, Role.ADMIN);
        if (userRepository.existsByUsername(newUser.getUsername())) {
            throw new DuplicateResourceException("Username already exists: " + newUser.getUsername());
        }
        userRepository.save(newUser);
        return newUser;
    }

    public boolean hasRole(User user, Role role) {
        return user.getRole() == role;
    }

    public User requireRole(User user, Role role) {
        if (!hasRole(user, role)) {
            throw new ForbiddenException("User lacks required role: " + role);
        }
        return user;
    }

    public User requireAnyRole(User user, Role... roles) {
        for (Role role : roles) {
            if (hasRole(user, role)) {
                return user;
            }
        }
        throw new ForbiddenException("User lacks required role");
    }
}
