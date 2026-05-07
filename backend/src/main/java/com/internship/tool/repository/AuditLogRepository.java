// AuditLogRepository.java: Spring Data JPA repository for the AuditLog entity.
// Provides basic CRUD and lookup by entity ID for audit trail queries.

package com.internship.tool.repository;

import com.internship.tool.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    /**
     * Returns all audit log entries for a given entity ID.
     */
    List<AuditLog> findByEntityIdOrderByPerformedAtDesc(UUID entityId);

    /**
     * Returns all audit log entries for a given entity type.
     */
    List<AuditLog> findByEntityTypeOrderByPerformedAtDesc(String entityType);
}
