package com.suuka.cleaning.auth.controller;

import com.suuka.cleaning.auth.dto.*;
import com.suuka.cleaning.auth.security.SuukaPrincipal;
import com.suuka.cleaning.auth.service.AuthService;
import com.suuka.cleaning.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success("Registration successful", authService.register(request));
    }

    @PostMapping("/register/client")
    public ApiResponse<AuthResponse> registerClient(@Valid @RequestBody ClientRegisterRequest request) {
        return ApiResponse.success("Client registration successful", authService.registerClient(request));
    }

    @PostMapping("/register/cleaner-application")
    public ApiResponse<AuthStatusResponse> registerCleanerApplication(@Valid @RequestBody CleanerApplicationRequest request) {
        return ApiResponse.success("Cleaner application submitted", authService.registerCleanerApplication(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success("Login successful", authService.login(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.success("Token refreshed", authService.refresh(request));
    }

    @PostMapping("/refresh-token")
    public ApiResponse<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.success("Token refreshed", authService.refresh(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody(required = false) RefreshTokenRequest request) {
        if (request != null) {
            authService.logoutToken(request.refreshToken());
        }
        return ApiResponse.success("Logout successful", null);
    }

    @PostMapping("/logout-all")
    public ApiResponse<Void> logoutAll(@AuthenticationPrincipal SuukaPrincipal principal) {
        authService.logoutAll(principal.getId());
        return ApiResponse.success("All sessions logged out", null);
    }

    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ApiResponse.success("If an account exists, password reset instructions will be sent", null);
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.success("Password reset successful", null);
    }

    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@AuthenticationPrincipal SuukaPrincipal principal, @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(principal.getUsername(), request);
        return ApiResponse.success("Password changed", null);
    }

    @PostMapping("/verify-account")
    public ApiResponse<AuthStatusResponse> verifyAccount(@Valid @RequestBody VerifyCodeRequest request) {
        return ApiResponse.success("Account verified", authService.verifyAccount(request));
    }

    @PostMapping("/resend-verification-code")
    public ApiResponse<Void> resendVerificationCode(@Valid @RequestBody ResendCodeRequest request) {
        authService.resendVerificationCode(request);
        return ApiResponse.success("Verification code sent", null);
    }

    @PostMapping("/verify-mfa")
    public ApiResponse<AuthResponse> verifyMfa(@Valid @RequestBody MfaVerifyRequest request) {
        return ApiResponse.success("MFA verified", authService.verifyMfa(request));
    }

    @PostMapping("/resend-mfa")
    public ApiResponse<Void> resendMfa(@Valid @RequestBody ResendCodeRequest request) {
        authService.resendMfa(request);
        return ApiResponse.success("MFA code sent", null);
    }

    @GetMapping("/me")
    public ApiResponse<AuthStatusResponse> me(@AuthenticationPrincipal SuukaPrincipal principal) {
        return ApiResponse.success("Current user loaded", authService.me(principal.getUsername()));
    }

    @GetMapping("/sessions")
    public ApiResponse<List<AuthSessionDto>> sessions(@AuthenticationPrincipal SuukaPrincipal principal) {
        return ApiResponse.success("Sessions loaded", authService.sessions(principal.getId()));
    }

    @DeleteMapping("/sessions/{id}")
    public ApiResponse<Void> deleteSession(@AuthenticationPrincipal SuukaPrincipal principal, @PathVariable UUID id) {
        authService.logout(id, principal.getId());
        return ApiResponse.success("Session revoked", null);
    }
}
