package com.goldtrust.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {
    List<AuditEvent> findByEntityNameAndEntityIdOrderByCreatedAtDesc(String entityName, String entityId);
    List<AuditEvent> findTop100ByOrderByCreatedAtDesc();
}

