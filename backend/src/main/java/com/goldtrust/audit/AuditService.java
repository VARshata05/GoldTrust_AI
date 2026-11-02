package com.goldtrust.audit;

import com.goldtrust.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditEventRepository auditRepository;

    @Transactional
    public void recordEvent(User user, String action, String entityName, String entityId, String oldValue, String newValue, String ipAddress) {
        AuditEvent event = AuditEvent.builder()
                .user(user)
                .username(user != null ? user.getUsername() : "SYSTEM")
                .action(action)
                .entityName(entityName)
                .entityId(entityId)
                .oldValue(oldValue)
                .newValue(newValue)
                .ipAddress(ipAddress)
                .createdAt(Instant.now())
                .build();
        auditRepository.save(event);
    }

    @Transactional(readOnly = true)
    public List<AuditEvent> getRecentAuditEvents() {
        return auditRepository.findTop100ByOrderByCreatedAtDesc();
    }
}

