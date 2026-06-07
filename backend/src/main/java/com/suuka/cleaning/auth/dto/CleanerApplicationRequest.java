package com.suuka.cleaning.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CleanerApplicationRequest(
        @NotBlank String fullName,
        @Email @NotBlank String email,
        @NotBlank String phoneNumber,
        @NotBlank String nationalId,
        @NotBlank String location,
        @NotBlank String experienceLevel,
        @NotBlank String availability,
        @Size(min = 8) String password,
        String idDocumentName,
        String profilePhotoName
) {
}
