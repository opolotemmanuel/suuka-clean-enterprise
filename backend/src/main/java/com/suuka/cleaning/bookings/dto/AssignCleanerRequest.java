package com.suuka.cleaning.bookings.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignCleanerRequest(@NotNull UUID cleanerId) {
}
