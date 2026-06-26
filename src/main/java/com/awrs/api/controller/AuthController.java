package com.awrs.api.controller;

import com.awrs.api.dto.LoginRequest;
import com.awrs.api.dto.LoginResponse;
import com.awrs.model.User;
import com.awrs.service.AuthService;
import com.awrs.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final SessionService sessionService;

    public AuthController(AuthService authService, SessionService sessionService) {
        this.authService = authService;
        this.sessionService = sessionService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = authService.login(request.username(), request.password());
        String token = sessionService.createSession(user);
        return ResponseEntity.ok(LoginResponse.from(token, user));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        sessionService.invalidate(token);
        return ResponseEntity.noContent().build();
    }
}
