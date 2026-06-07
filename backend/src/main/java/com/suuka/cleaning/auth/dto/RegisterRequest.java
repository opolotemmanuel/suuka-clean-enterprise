package com.suuka.cleaning.auth.dto;

import com.suuka.cleaning.common.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank String fullName,
        @Email @NotBlank String email,
        @Size(min = 8) String password,
        @Deprecated
        Role role,
        String branch,
        String zone
) {
}
