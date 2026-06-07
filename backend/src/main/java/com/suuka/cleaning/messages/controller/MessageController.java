package com.suuka.cleaning.messages.controller;

import com.suuka.cleaning.audit.service.AuditService;
import com.suuka.cleaning.auth.security.SuukaPrincipal;
import com.suuka.cleaning.common.response.ApiResponse;
import com.suuka.cleaning.messages.dto.ConversationDetail;
import com.suuka.cleaning.messages.dto.OpenConversationRequest;
import com.suuka.cleaning.messages.dto.SendMessageRequest;
import com.suuka.cleaning.messages.entity.Conversation;
import com.suuka.cleaning.messages.entity.Message;
import com.suuka.cleaning.messages.repository.ConversationRepository;
import com.suuka.cleaning.messages.repository.MessageRepository;
import com.suuka.cleaning.notifications.entity.Notification;
import com.suuka.cleaning.notifications.repository.NotificationRepository;
import com.suuka.cleaning.users.entity.User;
import com.suuka.cleaning.users.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public MessageController(
            ConversationRepository conversationRepository,
            MessageRepository messageRepository,
            NotificationRepository notificationRepository,
            UserRepository userRepository,
            AuditService auditService
    ) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @PostMapping("/conversations")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ApiResponse<Conversation> openConversation(
            @AuthenticationPrincipal SuukaPrincipal principal,
            @Valid @RequestBody OpenConversationRequest request
    ) {
        UUID first = min(principal.getId(), request.recipientId());
        UUID second = max(principal.getId(), request.recipientId());
        Conversation conversation = conversationRepository
                .findByParticipantOneIdAndParticipantTwoIdAndRelatedModuleAndRelatedEntityId(
                        first,
                        second,
                        request.relatedModule(),
                        request.relatedEntityId()
                )
                .orElseGet(() -> {
                    Conversation created = new Conversation();
                    created.setParticipantOneId(first);
                    created.setParticipantTwoId(second);
                    created.setRelatedModule(request.relatedModule());
                    created.setRelatedEntityId(request.relatedEntityId());
                    return conversationRepository.save(created);
                });
        auditService.record(principal.getId().toString(), "messages", "CONVERSATION_OPENED", conversation.getId().toString());
        return ApiResponse.success("Conversation opened", conversation);
    }

    @GetMapping("/conversations")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<Conversation>> conversations(@AuthenticationPrincipal SuukaPrincipal principal) {
        return ApiResponse.success(
                "Conversations loaded",
                conversationRepository.findByParticipantOneIdOrParticipantTwoId(principal.getId(), principal.getId())
        );
    }

    @GetMapping("/unread-count")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Long> unreadCount(@AuthenticationPrincipal SuukaPrincipal principal) {
        return ApiResponse.success("Unread message count loaded", messageRepository.countByRecipientIdAndReadFalse(principal.getId()));
    }

    @GetMapping("/conversations/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<ConversationDetail> conversation(@AuthenticationPrincipal SuukaPrincipal principal, @PathVariable UUID id) {
        Conversation conversation = requireConversationAccess(principal.getId(), id);
        List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(id);
        return ApiResponse.success("Conversation loaded", new ConversationDetail(conversation, messages));
    }

    @PostMapping("/conversations/{id}/messages")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ApiResponse<Message> sendMessage(
            @AuthenticationPrincipal SuukaPrincipal principal,
            @PathVariable UUID id,
            @Valid @RequestBody SendMessageRequest request
    ) {
        Conversation conversation = requireConversationAccess(principal.getId(), id);
        UUID recipientId = conversation.getParticipantOneId().equals(principal.getId())
                ? conversation.getParticipantTwoId()
                : conversation.getParticipantOneId();
        Message message = new Message();
        message.setConversationId(id);
        message.setSenderId(principal.getId());
        message.setRecipientId(recipientId);
        message.setBody(request.body());
        Message saved = messageRepository.save(message);
        createMessageNotification(principal, recipientId, id, request.body());
        auditService.record(principal.getId().toString(), "messages", "MESSAGE_SENT", id.toString());
        return ApiResponse.success("Message sent", saved);
    }

    @PatchMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ApiResponse<Message> markRead(@AuthenticationPrincipal SuukaPrincipal principal, @PathVariable UUID id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));
        if (!message.getRecipientId().equals(principal.getId())) {
            throw new IllegalArgumentException("Message does not belong to this user");
        }
        message.setRead(true);
        auditService.record(principal.getId().toString(), "messages", "MESSAGE_READ", id.toString());
        return ApiResponse.success("Message marked as read", message);
    }

    @PatchMapping("/conversations/{id}/read")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ApiResponse<List<Message>> markConversationRead(@AuthenticationPrincipal SuukaPrincipal principal, @PathVariable UUID id) {
        requireConversationAccess(principal.getId(), id);
        List<Message> unreadMessages = messageRepository.findByConversationIdAndRecipientIdAndReadFalse(id, principal.getId());
        unreadMessages.forEach(message -> message.setRead(true));
        auditService.record(principal.getId().toString(), "messages", "CONVERSATION_READ", id.toString());
        return ApiResponse.success("Conversation marked as read", messageRepository.saveAll(unreadMessages));
    }

    private Conversation requireConversationAccess(UUID userId, UUID conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));
        if (!conversation.includes(userId)) {
            throw new IllegalArgumentException("Conversation does not belong to this user");
        }
        return conversation;
    }

    private void createMessageNotification(SuukaPrincipal principal, UUID recipientId, UUID conversationId, String body) {
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new IllegalArgumentException("Message recipient not found"));
        Notification notification = new Notification();
        notification.setUserId(recipientId);
        notification.setTargetRole(recipient.getRole());
        notification.setType("MESSAGE");
        notification.setTitle("New message from " + principal.getUsername());
        notification.setMessage(body.length() > 180 ? body.substring(0, 180) + "..." : body);
        notification.setRelatedModule("MESSAGES");
        notification.setRelatedEntityId(conversationId.toString());
        notification.setAvailableActions(List.of("OPEN_CONVERSATION", "MARK_READ"));
        notificationRepository.save(notification);
    }

    private UUID min(UUID first, UUID second) {
        return Comparator.<UUID>naturalOrder().compare(first, second) <= 0 ? first : second;
    }

    private UUID max(UUID first, UUID second) {
        return Comparator.<UUID>naturalOrder().compare(first, second) >= 0 ? first : second;
    }
}
