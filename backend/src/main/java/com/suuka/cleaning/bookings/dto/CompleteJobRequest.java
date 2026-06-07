package com.suuka.cleaning.bookings.dto;

import jakarta.validation.constraints.NotBlank;

public record CompleteJobRequest(@NotBlank String completionNotes) {
}
