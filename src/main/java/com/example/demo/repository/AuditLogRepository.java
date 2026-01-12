package com.example.demo.repository;

import com.example.demo.entity.AuditLog;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio reactivo para operaciones CRUD de logs de auditoría.
 */
@Repository
public interface AuditLogRepository extends ReactiveCrudRepository<AuditLog, Long> {
}

