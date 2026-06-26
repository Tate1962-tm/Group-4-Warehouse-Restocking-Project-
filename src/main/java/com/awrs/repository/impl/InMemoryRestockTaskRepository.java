package com.awrs.repository.impl;

import com.awrs.model.RestockTask;
import com.awrs.model.RestockTaskStatus;
import com.awrs.repository.RestockTaskRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryRestockTaskRepository implements RestockTaskRepository {

    private final Map<String, RestockTask> tasks = new ConcurrentHashMap<>();

    @Override
    public Optional<RestockTask> findById(String id) {
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public List<RestockTask> findAll() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<RestockTask> findByStatus(RestockTaskStatus status) {
        return tasks.values().stream()
                .filter(task -> task.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public void save(RestockTask task) {
        tasks.put(task.getId(), task);
    }
}
