package com.suuka.cleaning.platform.dto;

import jakarta.validation.constraints.NotBlank;

public record BusinessRecordRequest(
        @NotBlank String title,
        String description,
        String relatedEntityId,
        String ownerId,
        String metadataJson
) {
}
