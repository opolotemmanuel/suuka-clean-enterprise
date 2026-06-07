package com.suuka.cleaning.users.dto;

import com.suuka.cleaning.common.enums.Role;
import jakarta.validation.constraints.NotNull;

public record AssignRoleRequest(@NotNull Role role) {
}
