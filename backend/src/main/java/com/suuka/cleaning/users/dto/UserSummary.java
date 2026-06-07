package com.suuka.cleaning.users.dto;

import com.suuka.cleaning.common.enums.Permission;
import com.suuka.cleaning.common.enums.Role;
import com.suuka.cleaning.users.entity.User;

import java.util.Set;
import java.util.UUID;

public record UserSummary(
        UUID id,
        String fullName,
        String email,
        Role role,
        Set<Permission> permissions,
        String branch,
        String zone,
        String phoneNumber,
        String accountStatus,
        String profilePictureUrl
) {
    public static UserSummary from(User user) {
        return new UserSummary(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getPermissions(),
                user.getBranch(),
                user.getZone(),
                user.getPhoneNumber(),
                user.getStatus().name(),
                user.getProfilePictureUrl()
        );
    }
}
