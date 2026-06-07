package com.suuka.cleaning.notifications.dto;

import com.suuka.cleaning.common.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateNotificationRequest(
        @NotNull UUID userId,
        @NotNull Role targetRole,
        @NotBlank String type,
        @NotBlank String title,
        @NotBlank String message,
        @NotBlank String relatedModule,
        String relatedEntityId,
        List<String> availableActions
) {
}
