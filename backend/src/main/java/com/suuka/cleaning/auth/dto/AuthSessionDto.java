package com.suuka.cleaning.auth.dto;

import com.suuka.cleaning.auth.entity.AuthSession;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuthSessionDto(UUID id, String deviceLabel, LocalDateTime expiresAt, LocalDateTime revokedAt, LocalDateTime createdAt) {
    public static AuthSessionDto from(AuthSession session) {
        return new AuthSessionDto(session.getId(), session.getDeviceLabel(), session.getExpiresAt(), session.getRevokedAt(), session.getCreatedAt());
    }
}
