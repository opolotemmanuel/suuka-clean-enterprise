package com.suuka.cleaning.platform.dto;

import jakarta.validation.constraints.NotBlank;

public record TimelineEventRequest(@NotBlank String eventType, String details) {
}
