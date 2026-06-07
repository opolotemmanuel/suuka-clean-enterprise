package com.suuka.cleaning.bookings.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateBookingRequest(
        @NotBlank String serviceType,
        @NotBlank String propertyAddress,
        double latitude,
        double longitude,
        @NotNull @Future LocalDateTime scheduledAt,
        @Min(1) int durationHours,
        @NotBlank String paymentMethod
) {
}
