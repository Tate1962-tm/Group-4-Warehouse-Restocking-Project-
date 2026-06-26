package com.awrs.repository;

import com.awrs.model.AuditLog;

import java.util.List;

public interface AuditLogRepository {

    void save(AuditLog log);

    List<AuditLog> findAll();
}
