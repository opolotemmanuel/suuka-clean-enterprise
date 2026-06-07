package com.suuka.cleaning.controller;

import com.suuka.cleaning.audit.service.AuditService;
import com.suuka.cleaning.auth.security.SuukaPrincipal;
import com.suuka.cleaning.common.response.ApiResponse;
import com.suuka.cleaning.model.ChatbotAuditLog;
import com.suuka.cleaning.model.ChatbotRequest;
import com.suuka.cleaning.model.ChatbotResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {
    private final AuditService auditService;

    public ChatbotController(AuditService auditService) {
        this.auditService = auditService;
    }

    private static final List<String> CRITICAL_ACTIONS = List.of(
            "approve refund",
            "approve refunds",
            "process refund",
            "suspend",
            "delete",
            "change price",
            "send campaign",
            "process payment",
            "purchase inventory",
            "modify permissions",
            "change system settings",
            "terminate",
            "pay cleaner"
    );

    private static final Map<String, List<String>> DENIED_TOPICS = Map.ofEntries(
            Map.entry("client", List.of("cleaner earning", "other client", "admin report", "company revenue", "inventory", "ai intelligence", "audit log", "system setting")),
            Map.entry("cleaner", List.of("client financial", "other cleaner", "admin analytics", "company revenue", "inventory purchase", "audit log", "system setting")),
            Map.entry("supervisor", List.of("payroll", "finance report", "system setting", "ai configuration", "audit log")),
            Map.entry("operations", List.of("payroll", "full financial", "system setting", "ai configuration", "audit log")),
            Map.entry("customer-success", List.of("cleaner payroll", "inventory purchase", "system setting", "audit log")),
            Map.entry("workforce", List.of("client financial", "revenue analytics", "system setting", "audit log")),
            Map.entry("finance", List.of("ai system setting", "user deletion", "cleaner suspension", "system setting")),
            Map.entry("inventory-procurement", List.of("payroll", "client financial", "user deletion", "system setting", "audit log")),
            Map.entry("admin", List.of()),
            Map.entry("executive-admin", List.of())
    );

    private static final List<ChatbotAuditLog> AUDIT_LOGS = new CopyOnWriteArrayList<>();

    @PostMapping("/message")
    @PreAuthorize("hasAuthority('USE_CHATBOT')")
    public ApiResponse<ChatbotResponse> answer(@AuthenticationPrincipal SuukaPrincipal principal, @Valid @RequestBody ChatbotRequest request) {
        ChatbotRequest securedRequest = bindToPrincipal(principal, request);
        ChatbotResponse response = evaluate(securedRequest);
        AUDIT_LOGS.add(0, new ChatbotAuditLog(
                Instant.now(),
                securedRequest.getUserId(),
                securedRequest.getRole(),
                securedRequest.getQuestion(),
                response.getDataAccessed(),
                response.getResponseType(),
                response.getPermissionResult(),
                response.getActionRequested(),
                response.getStatus()
        ));
        auditService.record(principal.getId().toString(), "chatbot", response.getResponseType(), response.getAnswer());
        return ApiResponse.success("Chatbot response generated", response);
    }

    @GetMapping("/audit-logs")
    @PreAuthorize("hasAuthority('VIEW_AUDIT_LOGS')")
    public ApiResponse<List<ChatbotAuditLog>> auditLogs() {
        return ApiResponse.success("Chatbot audit logs loaded", AUDIT_LOGS.stream().limit(100).toList());
    }

    private ChatbotRequest bindToPrincipal(SuukaPrincipal principal, ChatbotRequest request) {
        request.setUserId(principal.getId().toString());
        request.setRole(principal.getRole().name().toLowerCase(Locale.ROOT).replace('_', '-'));
        request.setBranch(principal.getBranch());
        request.setPermissionScope(principal.getAuthorities().stream().map(Object::toString).toList());
        return request;
    }

    private ChatbotResponse evaluate(ChatbotRequest request) {
        String normalizedRole = Optional.ofNullable(request.getRole()).orElse("").toLowerCase(Locale.ROOT);
        String question = Optional.ofNullable(request.getQuestion()).orElse("").toLowerCase(Locale.ROOT);
        String action = detectAction(question);
        ChatbotResponse response = new ChatbotResponse();
        response.setActionRequested(action);
        response.setStatus("recorded");

        if (!DENIED_TOPICS.containsKey(normalizedRole)) {
            response.setAnswer("You do not have permission to access this information.");
            response.setResponseType("denied");
            response.setPermissionResult("blocked");
            response.setDataAccessed("unknown_role");
            return response;
        }

        if (!"information_request".equals(action)) {
            response.setAnswer("This action requires authorized human approval. I can prepare a recommendation for review.");
            response.setResponseType("approval_required");
            response.setPermissionResult("blocked");
            response.setDataAccessed("critical_action");
            return response;
        }

        boolean denied = DENIED_TOPICS.get(normalizedRole).stream().anyMatch(question::contains);
        if (denied) {
            response.setAnswer("You do not have permission to access this information.");
            response.setResponseType("denied");
            response.setPermissionResult("blocked");
            response.setDataAccessed("restricted_topic");
            return response;
        }

        String dataAccessed = request.getPermissionScope() == null || request.getPermissionScope().isEmpty()
                ? "role_scope"
                : request.getPermissionScope().get(0);
        response.setAnswer("Permission check passed. I can answer within your " + normalizedRole + " scope for " + dataAccessed + ".");
        response.setResponseType("allowed");
        response.setPermissionResult("allowed");
        response.setDataAccessed(dataAccessed);
        return response;
    }

    private String detectAction(String question) {
        return CRITICAL_ACTIONS.stream()
                .filter(question::contains)
                .findFirst()
                .orElse("information_request");
    }
}
