package com.suuka.cleaning.users.service;

import com.suuka.cleaning.common.enums.Role;
import com.suuka.cleaning.users.dto.UserSummary;
import com.suuka.cleaning.users.entity.User;
import com.suuka.cleaning.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PermissionService permissionService;

    public UserService(UserRepository userRepository, PermissionService permissionService) {
        this.userRepository = userRepository;
        this.permissionService = permissionService;
    }

    public List<UserSummary> getAllUsers() {
        return userRepository.findAll().stream().map(UserSummary::from).toList();
    }

    @Transactional
    public UserSummary assignRole(UUID userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setRole(role);
        user.setPermissions(permissionService.permissionsFor(role));
        return UserSummary.from(user);
    }
}
