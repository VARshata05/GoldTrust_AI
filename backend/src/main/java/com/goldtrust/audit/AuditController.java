package com.goldtrust.audit;

import com.goldtrust.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
@Tag(name = "Audit Trails", description = "Enterprise regulatory compliance and audit logs")
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audit trail events (Admin only)")
    public ResponseEntity<ApiResponse<List<AuditEvent>>> getAuditLogs() {
        return ResponseEntity.ok(ApiResponse.ok(auditService.getRecentAuditEvents()));
    }
}

