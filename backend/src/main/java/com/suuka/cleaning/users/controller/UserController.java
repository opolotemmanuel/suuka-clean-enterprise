package com.suuka.cleaning.users.controller;

import com.suuka.cleaning.common.response.ApiResponse;
import com.suuka.cleaning.users.dto.AssignRoleRequest;
import com.suuka.cleaning.users.dto.UserSummary;
import com.suuka.cleaning.users.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('MANAGE_SYSTEM_SETTINGS')")
    public ApiResponse<List<UserSummary>> users() {
        return ApiResponse.success("Users loaded", userService.getAllUsers());
    }

    @PutMapping("/{userId}/role")
    @PreAuthorize("hasAuthority('MANAGE_SYSTEM_SETTINGS')")
    public ApiResponse<UserSummary> assignRole(@PathVariable UUID userId, @Valid @RequestBody AssignRoleRequest request) {
        return ApiResponse.success("Role assigned", userService.assignRole(userId, request.role()));
    }
}
