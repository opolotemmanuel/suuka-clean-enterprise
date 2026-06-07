package com.suuka.cleaning.auth.dto;

import com.suuka.cleaning.auth.enums.CleanerApplicationStatus;
import com.suuka.cleaning.users.dto.UserSummary;

import java.time.LocalDateTime;
import java.util.List;

public record AuthStatusResponse(
        UserSummary user,
        boolean mfaRequired,
        boolean accountVerified,
        LocalDateTime lockedUntil,
        CleanerApplicationStatus cleanerApplicationStatus,
        String reviewNotes,
        List<String> submittedDocuments
) {
}
