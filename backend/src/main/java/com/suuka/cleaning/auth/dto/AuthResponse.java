package com.suuka.cleaning.auth.dto;

import com.suuka.cleaning.users.dto.UserSummary;

public record AuthResponse(String accessToken, String refreshToken, String tokenType, UserSummary user) {
}
