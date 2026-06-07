package com.suuka.cleaning.auth.service;

import com.suuka.cleaning.auth.dto.*;
import com.suuka.cleaning.auth.entity.AuthSession;
import com.suuka.cleaning.auth.entity.CleanerApplication;
import com.suuka.cleaning.auth.repository.AuthSessionRepository;
import com.suuka.cleaning.auth.repository.CleanerApplicationRepository;
import com.suuka.cleaning.auth.security.JwtService;
import com.suuka.cleaning.audit.service.AuditService;
import com.suuka.cleaning.common.enums.RecordStatus;
import com.suuka.cleaning.common.enums.Role;
import com.suuka.cleaning.users.dto.UserSummary;
import com.suuka.cleaning.users.entity.User;
import com.suuka.cleaning.users.repository.UserRepository;
import com.suuka.cleaning.users.service.PermissionService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final AuthSessionRepository authSessionRepository;
    private final CleanerApplicationRepository cleanerApplicationRepository;
    private final PermissionService permissionService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuditService auditService;

    public AuthService(
            UserRepository userRepository,
            AuthSessionRepository authSessionRepository,
            CleanerApplicationRepository cleanerApplicationRepository,
            PermissionService permissionService,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            AuditService auditService
    ) {
        this.userRepository = userRepository;
        this.authSessionRepository = authSessionRepository;
        this.cleanerApplicationRepository = cleanerApplicationRepository;
        this.permissionService = permissionService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.auditService = auditService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        requireUniqueEmail(request.email());
        User user = baseUser(request.fullName(), request.email(), request.password(), Role.CLIENT);
        user.setBranch(request.branch());
        user.setZone(request.zone());
        user.setAccountVerified(true);
        userRepository.save(user);
        audit(user, "REGISTER_LEGACY");
        return tokens(user);
    }

    @Transactional
    public AuthResponse registerClient(ClientRegisterRequest request) {
        requireUniqueEmail(request.email());
        User user = baseUser(request.fullName(), request.email(), request.password(), Role.CLIENT);
        user.setPhoneNumber(request.phoneNumber());
        user.setAddress(request.address());
        user.setZone(request.zone());
        issueVerificationCode(user);
        userRepository.save(user);
        audit(user, "CLIENT_REGISTERED");
        return tokens(user);
    }

    @Transactional
    public AuthStatusResponse registerCleanerApplication(CleanerApplicationRequest request) {
        requireUniqueEmail(request.email());
        User user = baseUser(request.fullName(), request.email(), request.password(), Role.CLEANER);
        user.setPhoneNumber(request.phoneNumber());
        user.setZone(request.location());
        user.setStatus(RecordStatus.PENDING);
        issueVerificationCode(user);
        userRepository.save(user);

        CleanerApplication application = new CleanerApplication();
        application.setUserId(user.getId());
        application.setPhoneNumber(request.phoneNumber());
        application.setNationalId(request.nationalId());
        application.setLocation(request.location());
        application.setExperienceLevel(request.experienceLevel());
        application.setAvailability(request.availability());
        application.setIdDocumentName(request.idDocumentName());
        application.setProfilePhotoName(request.profilePhotoName());
        cleanerApplicationRepository.save(application);
        audit(user, "CLEANER_APPLICATION_CREATED");
        return status(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            audit(user, "LOGIN_BLOCKED_LOCKED");
            throw new IllegalStateException("Account temporarily locked");
        }
        if (user.getStatus() == RecordStatus.SUSPENDED || user.getStatus() == RecordStatus.ARCHIVED) {
            audit(user, "LOGIN_BLOCKED_STATUS");
            throw new IllegalStateException("Account is not active");
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
            user = userRepository.findByEmailIgnoreCase(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        } catch (RuntimeException ex) {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            audit(user, "LOGIN_FAILURE");
            if (user.getFailedLoginAttempts() >= 5) {
                user.setLockedUntil(LocalDateTime.now().plusMinutes(15));
                audit(user, "ACCOUNT_LOCKED");
            }
            throw ex;
        }
        user.setFailedLoginAttempts(0);
        if (requiresMfa(user)) {
            issueMfaCode(user);
        }
        audit(user, "LOGIN_SUCCESS");
        return tokens(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        if (!jwtService.isValid(request.refreshToken()) || !"refresh".equals(jwtService.type(request.refreshToken()))) {
            auditService.record(null, "auth", "SESSION_EXPIRED", "Invalid refresh token");
            throw new IllegalArgumentException("Invalid refresh token");
        }
        authSessionRepository.findByRefreshTokenHash(hash(request.refreshToken()))
                .filter(session -> session.getRevokedAt() == null)
                .orElseThrow(() -> new IllegalArgumentException("Session is no longer active"));
        User user = userRepository.findByEmailIgnoreCase(jwtService.subject(request.refreshToken()))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return tokens(user);
    }

    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        audit(user, "PASSWORD_CHANGED");
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmailIgnoreCase(request.email()).ifPresent(user -> {
            user.setResetToken(UUID.randomUUID().toString());
            user.setResetTokenExpiresAt(LocalDateTime.now().plusMinutes(15));
            audit(user, "PASSWORD_RESET_REQUESTED");
        });
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findAll().stream()
                .filter(candidate -> request.token().equals(candidate.getResetToken()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid reset token"));
        if (user.getResetTokenExpiresAt() == null || user.getResetTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Reset token expired");
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiresAt(null);
        logoutAll(user.getId());
        audit(user, "PASSWORD_RESET_COMPLETED");
    }

    @Transactional
    public AuthStatusResponse verifyAccount(VerifyCodeRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!isCodeValid(user.getVerificationCode(), user.getVerificationCodeExpiresAt(), request.code())) {
            user.setVerificationAttempts(user.getVerificationAttempts() + 1);
            audit(user, "ACCOUNT_VERIFICATION_FAILURE");
            throw new IllegalArgumentException("Invalid or expired code");
        }
        if (user.getVerificationAttempts() >= 3) {
            throw new IllegalStateException("Account verification temporarily locked");
        }
        user.setAccountVerified(true);
        user.setVerificationAttempts(0);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);
        audit(user, "ACCOUNT_VERIFIED");
        return status(user);
    }

    @Transactional
    public void resendVerificationCode(ResendCodeRequest request) {
        userRepository.findByEmailIgnoreCase(request.email()).ifPresent(user -> {
            issueVerificationCode(user);
            audit(user, "VERIFICATION_CODE_SENT");
        });
    }

    @Transactional
    public AuthResponse verifyMfa(MfaVerifyRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.getMfaAttempts() >= 3) {
            throw new IllegalStateException("MFA verification temporarily locked");
        }
        if (!isCodeValid(user.getMfaCode(), user.getMfaCodeExpiresAt(), request.code())) {
            user.setMfaAttempts(user.getMfaAttempts() + 1);
            audit(user, "MFA_FAILURE");
            throw new IllegalArgumentException("Invalid or expired code");
        }
        user.setMfaAttempts(0);
        user.setMfaCode(null);
        user.setMfaCodeExpiresAt(null);
        audit(user, "MFA_SUCCESS");
        return tokens(user);
    }

    @Transactional
    public void resendMfa(ResendCodeRequest request) {
        userRepository.findByEmailIgnoreCase(request.email()).ifPresent(user -> {
            issueMfaCode(user);
            audit(user, "MFA_CODE_SENT");
        });
    }

    public AuthStatusResponse me(String email) {
        User user = userRepository.findByEmailIgnoreCase(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return status(user);
    }

    public List<AuthSessionDto> sessions(UUID userId) {
        return authSessionRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(AuthSessionDto::from).toList();
    }

    @Transactional
    public void logout(UUID sessionId, UUID userId) {
        AuthSession session = authSessionRepository.findById(sessionId).orElseThrow(() -> new IllegalArgumentException("Session not found"));
        if (!session.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Session does not belong to this user");
        }
        session.setRevokedAt(LocalDateTime.now());
        auditService.record(userId.toString(), "auth", "SESSION_REVOKED", sessionId.toString());
    }

    @Transactional
    public void logoutAll(UUID userId) {
        authSessionRepository.findByUserIdOrderByCreatedAtDesc(userId).forEach(session -> {
            if (session.getRevokedAt() == null) {
                session.setRevokedAt(LocalDateTime.now());
            }
        });
        auditService.record(userId.toString(), "auth", "LOGOUT_ALL", "All sessions revoked");
    }

    @Transactional
    public void logoutToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }
        authSessionRepository.findByRefreshTokenHash(hash(refreshToken)).ifPresent(session -> {
            session.setRevokedAt(LocalDateTime.now());
            auditService.record(session.getUserId().toString(), "auth", "LOGOUT", session.getId().toString());
        });
    }

    private AuthResponse tokens(User user) {
        String refresh = jwtService.createRefreshToken(user);
        AuthSession session = new AuthSession();
        session.setUserId(user.getId());
        session.setRefreshTokenHash(hash(refresh));
        session.setDeviceLabel("Web browser");
        session.setExpiresAt(LocalDateTime.now().plusDays(14));
        authSessionRepository.save(session);
        return new AuthResponse(jwtService.createAccessToken(user), refresh, "Bearer", UserSummary.from(user));
    }

    private User baseUser(String fullName, String email, String password, Role role) {
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email.toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);
        user.setPermissions(permissionService.permissionsFor(role));
        return user;
    }

    private void requireUniqueEmail(String email) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalStateException("Email is already registered");
        }
    }

    private boolean requiresMfa(User user) {
        return user.isMfaEnabled()
                || user.getRole() == Role.EXECUTIVE_ADMIN
                || user.getRole() == Role.SYSTEM_ADMIN
                || user.getRole() == Role.FINANCE_MANAGER;
    }

    private void issueVerificationCode(User user) {
        user.setVerificationCode("123456");
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(5));
        user.setVerificationAttempts(0);
    }

    private void issueMfaCode(User user) {
        user.setMfaCode("123456");
        user.setMfaCodeExpiresAt(LocalDateTime.now().plusMinutes(5));
        user.setMfaAttempts(0);
    }

    private boolean isCodeValid(String expected, LocalDateTime expiresAt, String actual) {
        return expected != null && expiresAt != null && !expiresAt.isBefore(LocalDateTime.now()) && expected.equals(actual);
    }

    private AuthStatusResponse status(User user) {
        CleanerApplication application = cleanerApplicationRepository.findByUserId(user.getId()).orElse(null);
        return new AuthStatusResponse(
                UserSummary.from(user),
                requiresMfa(user),
                user.isAccountVerified(),
                user.getLockedUntil(),
                application == null ? null : application.getApplicationStatus(),
                application == null ? null : application.getReviewNotes(),
                application == null ? List.of() : List.of(application.getIdDocumentName(), application.getProfilePhotoName()).stream()
                        .filter(item -> item != null && !item.isBlank())
                        .toList()
        );
    }

    private void audit(User user, String action) {
        auditService.record(user.getId() == null ? null : user.getId().toString(), "auth", action, user.getEmail());
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is not available", ex);
        }
    }
}
