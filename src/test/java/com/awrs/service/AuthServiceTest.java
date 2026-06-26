package com.awrs.service;

import com.awrs.exception.AuthenticationException;
import com.awrs.exception.ForbiddenException;
import com.awrs.model.Role;
import com.awrs.model.User;
import com.awrs.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository);
    }

    @Test
    void loginSuccess() {
        User user = new User("worker", "pass", Role.WORKER);
        when(userRepository.findByUsername("worker")).thenReturn(Optional.of(user));
        User result = authService.login("worker", "pass");
        assertEquals(Role.WORKER, result.getRole());
    }

    @Test
    void loginInvalidPassword() {
        User user = new User("worker", "pass", Role.WORKER);
        when(userRepository.findByUsername("worker")).thenReturn(Optional.of(user));
        assertThrows(AuthenticationException.class, () -> authService.login("worker", "wrong"));
    }

    @Test
    void loginUnknownUser() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());
        assertThrows(AuthenticationException.class, () -> authService.login("ghost", "pass"));
    }

    @Test
    void loginEmptyCredentials() {
        assertThrows(IllegalArgumentException.class, () -> authService.login("", "pass"));
    }

    @Test
    void nonAdminCannotCreateUsers() {
        User worker = new User("worker", "pass", Role.WORKER);
        User newUser = new User("newbie", "pass", Role.WORKER);
        assertThrows(ForbiddenException.class, () -> authService.createUser(worker, newUser));
    }
}
