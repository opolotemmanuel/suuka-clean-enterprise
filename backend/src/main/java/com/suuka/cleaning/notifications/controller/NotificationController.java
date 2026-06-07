package com.suuka.cleaning.notifications.controller;

import com.suuka.cleaning.audit.service.AuditService;
import com.suuka.cleaning.auth.security.SuukaPrincipal;
import com.suuka.cleaning.common.response.ApiResponse;
import com.suuka.cleaning.notifications.dto.CreateNotificationRequest;
import com.suuka.cleaning.notifications.entity.Notification;
import com.suuka.cleaning.notifications.repository.NotificationRepository;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationRepository notificationRepository;
    private final AuditService auditService;

    public NotificationController(NotificationRepository notificationRepository, AuditService auditService) {
        this.notificationRepository = notificationRepository;
        this.auditService = auditService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<Notification>> mine(@AuthenticationPrincipal SuukaPrincipal principal) {
        return ApiResponse.success("Notifications loaded", notificationRepository.findByUserIdOrderByCreatedAtDesc(principal.getId()));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MANAGE_SYSTEM_SETTINGS')")
    public ApiResponse<Notification> create(@Valid @RequestBody CreateNotificationRequest request) {
        Notification notification = new Notification();
        notification.setUserId(request.userId());
        notification.setTargetRole(request.targetRole());
        notification.setType(request.type());
        notification.setTitle(request.title());
        notification.setMessage(request.message());
        notification.setRelatedModule(request.relatedModule());
        notification.setRelatedEntityId(request.relatedEntityId());
        notification.setAvailableActions(request.availableActions() == null ? List.of() : request.availableActions());
        return ApiResponse.success("Notification created", notificationRepository.save(notification));
    }

    @PatchMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Notification> markRead(@AuthenticationPrincipal SuukaPrincipal principal, @PathVariable UUID id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        if (!notification.getUserId().equals(principal.getId())) {
            throw new IllegalArgumentException("Notification does not belong to this user");
        }
        notification.setRead(true);
        auditService.record(principal.getId().toString(), "notifications", "MARK_READ", id.toString());
        return ApiResponse.success("Notification marked as read", notificationRepository.save(notification));
    }
}
