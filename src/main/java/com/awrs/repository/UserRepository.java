package com.awrs.repository;

import com.awrs.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findByUsername(String username);

    List<User> findAll();

    void save(User user);

    void delete(String username);

    boolean existsByUsername(String username);
}
