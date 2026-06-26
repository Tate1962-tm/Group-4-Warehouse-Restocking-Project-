package com.awrs.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    @Test
    void validUserCreation() {
        User user = new User("alice", "secret", Role.WORKER);
        assertEquals("alice", user.getUsername());
        assertEquals(Role.WORKER, user.getRole());
    }

    @Test
    void emptyUsernameRejected() {
        assertThrows(IllegalArgumentException.class, () -> new User("", "secret", Role.WORKER));
    }

    @Test
    void authenticateWithCorrectPassword() {
        User user = new User("alice", "secret", Role.MANAGER);
        assertTrue(user.authenticate("secret"));
    }
}
