package com.awrs.service;

import com.awrs.exception.AuthenticationException;
import com.awrs.model.User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionService {

    private final Map<String, User> sessions = new ConcurrentHashMap<>();

    public String createSession(User user) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, user);
        return token;
    }

    public User requireUser(String token) {
        if (token == null || token.isBlank()) {
            throw new AuthenticationException("Authentication token is required");
        }
        String normalized = token.startsWith("Bearer ") ? token.substring(7) : token;
        return Optional.ofNullable(sessions.get(normalized))
                .orElseThrow(() -> new AuthenticationException("Invalid or expired token"));
    }

    public void invalidate(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token != null) {
            sessions.remove(token);
        }
    }
}
