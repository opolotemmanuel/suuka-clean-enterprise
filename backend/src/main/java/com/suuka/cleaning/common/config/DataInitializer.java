package com.suuka.cleaning.common.config;

import com.suuka.cleaning.common.enums.Permission;
import com.suuka.cleaning.common.enums.Role;
import com.suuka.cleaning.users.entity.RolePermission;
import com.suuka.cleaning.users.entity.User;
import com.suuka.cleaning.users.repository.RolePermissionRepository;
import com.suuka.cleaning.users.repository.UserRepository;
import com.suuka.cleaning.users.service.PermissionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;

@Configuration
public class DataInitializer {
    private static final String DEMO_PASSWORD = "Password123!";

    @Bean
    CommandLineRunner seedSecurityData(
            PermissionService permissionService,
            RolePermissionRepository rolePermissionRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${SUUKA_ADMIN_EMAIL:}") String adminEmail,
            @Value("${SUUKA_ADMIN_PASSWORD:}") String adminPassword
    ) {
        return args -> {
            for (Role role : Role.values()) {
                if (rolePermissionRepository.findByRole(role).isEmpty()) {
                    Set<Permission> permissions = permissionService.permissionsFor(role);
                    for (Permission permission : permissions) {
                        RolePermission rolePermission = new RolePermission();
                        rolePermission.setRole(role);
                        rolePermission.setPermission(permission);
                        rolePermissionRepository.save(rolePermission);
                    }
                }
            }

            if (!adminEmail.isBlank() && !adminPassword.isBlank() && !userRepository.existsByEmailIgnoreCase(adminEmail)) {
                seedUser(userRepository, passwordEncoder, permissionService, "Suuka Executive Admin", adminEmail, adminPassword, Role.EXECUTIVE_ADMIN, "GLOBAL");
            }

            for (DemoUser demoUser : demoUsers()) {
                if (!userRepository.existsByEmailIgnoreCase(demoUser.email())) {
                    seedUser(
                            userRepository,
                            passwordEncoder,
                            permissionService,
                            demoUser.fullName(),
                            demoUser.email(),
                            DEMO_PASSWORD,
                            demoUser.role(),
                            demoUser.zone()
                    );
                }
            }
        };
    }

    private static List<DemoUser> demoUsers() {
        return List.of(
                new DemoUser("Client User", "client@suukaclean.local", Role.CLIENT, "Central"),
                new DemoUser("Cleaner User", "cleaner@suukaclean.local", Role.CLEANER, "Central"),
                new DemoUser("Supervisor User", "supervisor@suukaclean.local", Role.SUPERVISOR, "Central"),
                new DemoUser("Operations Manager", "operations@suukaclean.local", Role.OPERATIONS_MANAGER, "Central"),
                new DemoUser("Customer Success Manager", "customer.success@suukaclean.local", Role.CUSTOMER_SUCCESS_MANAGER, "Central"),
                new DemoUser("HR Manager", "hr@suukaclean.local", Role.HR_MANAGER, "HQ"),
                new DemoUser("Finance Manager", "finance@suukaclean.local", Role.FINANCE_MANAGER, "HQ"),
                new DemoUser("Inventory Manager", "inventory@suukaclean.local", Role.INVENTORY_MANAGER, "HQ"),
                new DemoUser("System Admin", "system.admin@suukaclean.local", Role.SYSTEM_ADMIN, "GLOBAL"),
                new DemoUser("Executive Admin", "executive@suukaclean.local", Role.EXECUTIVE_ADMIN, "GLOBAL")
        );
    }

    private static void seedUser(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            PermissionService permissionService,
            String fullName,
            String email,
            String password,
            Role role,
            String zone
    ) {
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email.toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);
        user.setPermissions(permissionService.permissionsFor(role));
        user.setBranch("HQ");
        user.setZone(zone);
        user.setAccountVerified(true);
        userRepository.save(user);
    }

    private record DemoUser(String fullName, String email, Role role, String zone) {
    }
}
