package com.suuka.cleaning.shared.controller;

import com.suuka.cleaning.approvals.entity.ApprovalRequest;
import com.suuka.cleaning.approvals.repository.ApprovalRequestRepository;
import com.suuka.cleaning.approvals.service.ApprovalService;
import com.suuka.cleaning.audit.entity.AuditLog;
import com.suuka.cleaning.audit.service.AuditService;
import com.suuka.cleaning.auth.security.SuukaPrincipal;
import com.suuka.cleaning.common.enums.ApprovalStatus;
import com.suuka.cleaning.common.response.ApiResponse;
import com.suuka.cleaning.model.ChatbotRequest;
import com.suuka.cleaning.model.ChatbotResponse;
import com.suuka.cleaning.notifications.entity.Notification;
import com.suuka.cleaning.notifications.repository.NotificationRepository;
import com.suuka.cleaning.platform.entity.BusinessRecord;
import com.suuka.cleaning.platform.enums.PlatformModule;
import com.suuka.cleaning.platform.repository.BusinessRecordRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class SharedRequirementController {
    private final NotificationRepository notificationRepository;
    private final ApprovalRequestRepository approvalRequestRepository;
    private final ApprovalService approvalService;
    private final AuditService auditService;
    private final BusinessRecordRepository businessRecordRepository;

    public SharedRequirementController(
            NotificationRepository notificationRepository,
            ApprovalRequestRepository approvalRequestRepository,
            ApprovalService approvalService,
            AuditService auditService,
            BusinessRecordRepository businessRecordRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.approvalRequestRepository = approvalRequestRepository;
        this.approvalService = approvalService;
        this.auditService = auditService;
        this.businessRecordRepository = businessRecordRepository;
    }

    @GetMapping("/notifications/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Notification> notification(@AuthenticationPrincipal SuukaPrincipal principal, @PathVariable UUID id) {
        Notification notification = notificationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        if (!notification.getUserId().equals(principal.getId())) {
            throw new IllegalArgumentException("Notification does not belong to this user");
        }
        return ApiResponse.success("Notification loaded", notification);
    }

    @PostMapping("/notifications/{id}/action")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Notification> notificationAction(@AuthenticationPrincipal SuukaPrincipal principal, @PathVariable UUID id, @RequestBody(required = false) Map<String, Object> request) {
        Notification notification = notificationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        if (!notification.getUserId().equals(principal.getId())) {
            throw new IllegalArgumentException("Notification does not belong to this user");
        }
        notification.setRead(true);
        auditService.record(principal.getId().toString(), "notifications", "ACTION", request == null ? id.toString() : request.toString());
        return ApiResponse.success("Notification action recorded", notificationRepository.save(notification));
    }

    @PostMapping("/ai-chatbot/message")
    @PreAuthorize("hasAuthority('USE_CHATBOT')")
    public ApiResponse<ChatbotResponse> aiChatbotMessage(@AuthenticationPrincipal SuukaPrincipal principal, @RequestBody ChatbotRequest request) {
        auditService.record(principal.getId().toString(), "ai-chatbot", "MESSAGE", request.getQuestion());
        ChatbotResponse response = new ChatbotResponse();
        response.setAnswer("Thanks, " + principal.getRole().name().replace('_', ' ') + ". I recorded your request and can connect it to the right SUUKA module.");
        response.setResponseType("ROLE_ASSISTANT");
        response.setPermissionResult("ALLOWED");
        response.setDataAccessed("role_dashboard,shared_modules");
        response.setActionRequested(request.getRequestedAction());
        response.setStatus("SUCCESS");
        return ApiResponse.success("AI chatbot response generated", response);
    }

    @GetMapping("/ai-chatbot/conversations")
    @PreAuthorize("hasAuthority('USE_CHATBOT')")
    public ApiResponse<List<BusinessRecord>> aiChatbotConversations() {
        return ApiResponse.success("AI chatbot conversations loaded", businessRecordRepository.findByModuleOrderByCreatedAtDesc(PlatformModule.ACTIVITY_TIMELINE));
    }

    @GetMapping("/ai-chatbot/conversations/{id}")
    @PreAuthorize("hasAuthority('USE_CHATBOT')")
    public ApiResponse<BusinessRecord> aiChatbotConversation(@PathVariable UUID id) {
        return ApiResponse.success("AI chatbot conversation loaded", businessRecordRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Conversation not found")));
    }

    @GetMapping("/approvals")
    @PreAuthorize("hasAuthority('REVIEW_APPROVAL_REQUESTS')")
    public ApiResponse<List<ApprovalRequest>> approvals() {
        return ApiResponse.success("Approvals loaded", approvalService.all());
    }

    @GetMapping("/approvals/{id}")
    @PreAuthorize("hasAuthority('REVIEW_APPROVAL_REQUESTS')")
    public ApiResponse<ApprovalRequest> approval(@PathVariable UUID id) {
        return ApiResponse.success("Approval loaded", approvalRequestRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Approval not found")));
    }

    @PostMapping("/approvals/{id}/approve")
    @PreAuthorize("hasAuthority('REVIEW_APPROVAL_REQUESTS')")
    public ApiResponse<ApprovalRequest> approve(@AuthenticationPrincipal SuukaPrincipal principal, @PathVariable UUID id) {
        return ApiResponse.success("Approval approved", approvalService.review(principal.getId().toString(), id, ApprovalStatus.APPROVED));
    }

    @PostMapping("/approvals/{id}/reject")
    @PreAuthorize("hasAuthority('REVIEW_APPROVAL_REQUESTS')")
    public ApiResponse<ApprovalRequest> reject(@AuthenticationPrincipal SuukaPrincipal principal, @PathVariable UUID id) {
        return ApiResponse.success("Approval rejected", approvalService.review(principal.getId().toString(), id, ApprovalStatus.REJECTED));
    }

    @PostMapping("/approvals/{id}/escalate")
    @PreAuthorize("hasAuthority('REVIEW_APPROVAL_REQUESTS')")
    public ApiResponse<ApprovalRequest> escalate(@AuthenticationPrincipal SuukaPrincipal principal, @PathVariable UUID id) {
        return ApiResponse.success("Approval escalated", approvalService.review(principal.getId().toString(), id, ApprovalStatus.ESCALATED));
    }

    @GetMapping("/audit-logs")
    @PreAuthorize("hasAuthority('VIEW_AUDIT_LOGS')")
    public ApiResponse<List<AuditLog>> auditLogs() {
        return ApiResponse.success("Audit logs loaded", auditService.latest());
    }

    @GetMapping("/audit-logs/{entityType}/{entityId}")
    @PreAuthorize("hasAuthority('VIEW_AUDIT_LOGS')")
    public ApiResponse<List<AuditLog>> auditLogsForEntity(@PathVariable String entityType, @PathVariable String entityId) {
        List<AuditLog> logs = auditService.latest().stream()
                .filter(log -> entityType.equalsIgnoreCase(log.getModule()) || (log.getDetails() != null && log.getDetails().contains(entityId)))
                .toList();
        return ApiResponse.success("Entity audit logs loaded", logs);
    }

    @GetMapping({
            "/reports/bookings",
            "/reports/revenue",
            "/reports/clients",
            "/reports/cleaners",
            "/reports/inventory",
            "/reports/complaints",
            "/reports/ai"
    })
    @PreAuthorize("hasAuthority('VIEW_REPORTS')")
    public ApiResponse<List<BusinessRecord>> report() {
        return ApiResponse.success("Report loaded", businessRecordRepository.findByModuleOrderByCreatedAtDesc(PlatformModule.REPORTS));
    }
}
