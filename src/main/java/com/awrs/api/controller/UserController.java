package com.awrs.api.controller;

import com.awrs.api.dto.CreateUserRequest;
import com.awrs.api.dto.UserResponse;
import com.awrs.model.User;
import com.awrs.repository.UserRepository;
import com.awrs.service.AuthService;
import com.awrs.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final SessionService sessionService;

    public UserController(AuthService authService, UserRepository userRepository, SessionService sessionService) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.sessionService = sessionService;
    }

    @GetMapping
    public List<UserResponse> listUsers(@RequestHeader("Authorization") String token) {
        User admin = sessionService.requireUser(token);
        authService.requireRole(admin, com.awrs.model.Role.ADMIN);
        return userRepository.findAll().stream().map(UserResponse::from).toList();
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestHeader("Authorization") String token,
                                                   @Valid @RequestBody CreateUserRequest request) {
        User admin = sessionService.requireUser(token);
        User created = authService.createUser(admin, request.toUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(created));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@RequestHeader("Authorization") String token,
                                           @PathVariable String username) {
        User admin = sessionService.requireUser(token);
        authService.requireRole(admin, com.awrs.model.Role.ADMIN);
        if (admin.getUsername().equals(username)) {
            throw new IllegalArgumentException("Cannot delete your own account");
        }
        userRepository.delete(username);
        return ResponseEntity.noContent().build();
    }
}
