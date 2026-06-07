package com.suuka.cleaning.auth.dto;

import jakarta.validation.constraints.*;

public record ClientRegisterRequest(
        @NotBlank String fullName,
        @Email @NotBlank String email,
        @NotBlank String phoneNumber,
        @Size(min = 8) String password,
        @NotBlank String address,
        @NotBlank String zone,
        @AssertTrue boolean termsAccepted
) {
}
