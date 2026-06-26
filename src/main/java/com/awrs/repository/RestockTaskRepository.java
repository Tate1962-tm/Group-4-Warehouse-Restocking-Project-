package com.awrs.repository;

import com.awrs.model.RestockTask;
import com.awrs.model.RestockTaskStatus;

import java.util.List;
import java.util.Optional;

public interface RestockTaskRepository {

    Optional<RestockTask> findById(String id);

    List<RestockTask> findAll();

    List<RestockTask> findByStatus(RestockTaskStatus status);

    void save(RestockTask task);
}
