package com.suuka.cleaning.messages.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OpenConversationRequest(
        @NotNull UUID recipientId,
        String relatedModule,
        String relatedEntityId
) {
}
