package com.awrs.repository.impl;

import com.awrs.model.AuditLog;
import com.awrs.repository.AuditLogRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryAuditLogRepository implements AuditLogRepository {

    private final List<AuditLog> logs = new CopyOnWriteArrayList<>();

    @Override
    public void save(AuditLog log) {
        logs.add(log);
    }

    @Override
    public List<AuditLog> findAll() {
        return new ArrayList<>(logs);
    }
}
