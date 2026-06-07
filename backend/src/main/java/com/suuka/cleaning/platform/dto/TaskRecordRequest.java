package com.suuka.cleaning.platform.dto;

import com.suuka.cleaning.platform.enums.PlatformModule;
import com.suuka.cleaning.platform.enums.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TaskRecordRequest(
        @NotBlank String title,
        String description,
        String assignedTo,
        @NotNull PlatformModule relatedModule,
        String relatedEntityId,
        LocalDateTime dueDate,
        TaskPriority priority
) {
}
