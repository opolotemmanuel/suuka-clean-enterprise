package com.suuka.cleaning.audit.controller;

import com.suuka.cleaning.audit.entity.AuditLog;
import com.suuka.cleaning.audit.service.AuditService;
import com.suuka.cleaning.common.response.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/audit-logs")
public class AuditController {
    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_AUDIT_LOGS')")
    public ApiResponse<List<AuditLog>> auditLogs() {
        return ApiResponse.success("Audit logs loaded", auditService.latest());
    }
}
