package com.suuka.cleaning.platform.controller;

import com.suuka.cleaning.audit.service.AuditService;
import com.suuka.cleaning.auth.security.SuukaPrincipal;
import com.suuka.cleaning.common.response.ApiResponse;
import com.suuka.cleaning.platform.dto.BusinessRecordRequest;
import com.suuka.cleaning.platform.dto.TaskRecordRequest;
import com.suuka.cleaning.platform.dto.TimelineEventRequest;
import com.suuka.cleaning.platform.entity.ActivityTimelineEvent;
import com.suuka.cleaning.platform.entity.BusinessRecord;
import com.suuka.cleaning.platform.entity.TaskRecord;
import com.suuka.cleaning.platform.enums.PlatformModule;
import com.suuka.cleaning.platform.repository.ActivityTimelineEventRepository;
import com.suuka.cleaning.platform.repository.BusinessRecordRepository;
import com.suuka.cleaning.platform.repository.TaskRecordRepository;
import com.suuka.cleaning.platform.service.PlatformPermissionService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/platform")
public class PlatformRecordController {
    private final BusinessRecordRepository businessRecordRepository;
    private final TaskRecordRepository taskRecordRepository;
    private final ActivityTimelineEventRepository timelineRepository;
    private final PlatformPermissionService permissionService;
    private final AuditService auditService;

    public PlatformRecordController(
            BusinessRecordRepository businessRecordRepository,
            TaskRecordRepository taskRecordRepository,
            ActivityTimelineEventRepository timelineRepository,
            PlatformPermissionService permissionService,
            AuditService auditService
    ) {
        this.businessRecordRepository = businessRecordRepository;
        this.taskRecordRepository = taskRecordRepository;
        this.timelineRepository = timelineRepository;
        this.permissionService = permissionService;
        this.auditService = auditService;
    }

    @GetMapping("/{module}/records")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<BusinessRecord>> records(
            @AuthenticationPrincipal SuukaPrincipal principal,
            @PathVariable PlatformModule module
    ) {
        permissionService.requireRead(module, principal.getAuthorities());
        return ApiResponse.success("Records loaded", businessRecordRepository.findByModuleOrderByCreatedAtDesc(module));
    }

    @PostMapping("/{module}/records")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ApiResponse<BusinessRecord> createRecord(
            @AuthenticationPrincipal SuukaPrincipal principal,
            @PathVariable PlatformModule module,
            @Valid @RequestBody BusinessRecordRequest request
    ) {
        permissionService.requireWrite(module, principal.getAuthorities());
        BusinessRecord record = new BusinessRecord();
        record.setModule(module);
        record.setTitle(request.title());
        record.setDescription(request.description());
        record.setRelatedEntityId(request.relatedEntityId());
        record.setOwnerId(request.ownerId());
        record.setMetadataJson(request.metadataJson());
        BusinessRecord saved = businessRecordRepository.save(record);
        timeline(principal, module, saved.getId().toString(), "CREATED", request.title());
        auditService.record(principal.getId().toString(), module.name(), "RECORD_CREATED", saved.getId().toString());
        return ApiResponse.success("Record created", saved);
    }

    @PostMapping("/tasks")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ApiResponse<TaskRecord> createTask(
            @AuthenticationPrincipal SuukaPrincipal principal,
            @Valid @RequestBody TaskRecordRequest request
    ) {
        permissionService.requireWrite(PlatformModule.TASKS, principal.getAuthorities());
        TaskRecord task = new TaskRecord();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setAssignedTo(request.assignedTo() == null ? principal.getId().toString() : request.assignedTo());
        task.setRelatedModule(request.relatedModule());
        task.setRelatedEntityId(request.relatedEntityId());
        task.setDueDate(request.dueDate());
        if (request.priority() != null) {
            task.setPriority(request.priority());
        }
        TaskRecord saved = taskRecordRepository.save(task);
        timeline(principal, request.relatedModule(), request.relatedEntityId(), "TASK_CREATED", request.title());
        auditService.record(principal.getId().toString(), "tasks", "TASK_CREATED", saved.getId().toString());
        return ApiResponse.success("Task created", saved);
    }

    @GetMapping("/tasks")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<TaskRecord>> myTasks(@AuthenticationPrincipal SuukaPrincipal principal) {
        return ApiResponse.success("Tasks loaded", taskRecordRepository.findByAssignedToOrderByCreatedAtDesc(principal.getId().toString()));
    }

    @GetMapping("/{module}/{entityId}/timeline")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<ActivityTimelineEvent>> timeline(
            @AuthenticationPrincipal SuukaPrincipal principal,
            @PathVariable PlatformModule module,
            @PathVariable String entityId
    ) {
        permissionService.requireRead(module, principal.getAuthorities());
        return ApiResponse.success("Timeline loaded", timelineRepository.findByModuleAndEntityIdOrderByCreatedAtDesc(module, entityId));
    }

    @PostMapping("/{module}/{entityId}/timeline")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ApiResponse<ActivityTimelineEvent> addTimelineEvent(
            @AuthenticationPrincipal SuukaPrincipal principal,
            @PathVariable PlatformModule module,
            @PathVariable String entityId,
            @Valid @RequestBody TimelineEventRequest request
    ) {
        permissionService.requireWrite(module, principal.getAuthorities());
        ActivityTimelineEvent event = timeline(principal, module, entityId, request.eventType(), request.details());
        auditService.record(principal.getId().toString(), module.name(), "TIMELINE_EVENT_CREATED", entityId);
        return ApiResponse.success("Timeline event created", event);
    }

    private ActivityTimelineEvent timeline(SuukaPrincipal principal, PlatformModule module, String entityId, String eventType, String details) {
        ActivityTimelineEvent event = new ActivityTimelineEvent();
        event.setModule(module);
        event.setEntityId(entityId == null ? "none" : entityId);
        event.setEventType(eventType);
        event.setDetails(details);
        event.setActorId(principal.getId().toString());
        return timelineRepository.save(event);
    }
}
