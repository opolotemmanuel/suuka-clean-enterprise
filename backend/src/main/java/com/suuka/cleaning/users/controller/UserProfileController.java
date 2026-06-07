package com.suuka.cleaning.users.controller;

import com.suuka.cleaning.audit.service.AuditService;
import com.suuka.cleaning.auth.security.SuukaPrincipal;
import com.suuka.cleaning.common.response.ApiResponse;
import com.suuka.cleaning.users.dto.UserSummary;
import com.suuka.cleaning.users.entity.User;
import com.suuka.cleaning.users.repository.UserRepository;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserProfileController {
    private static final long MAX_PROFILE_IMAGE_BYTES = 5L * 1024L * 1024L;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final Map<String, String> ALLOWED_CONTENT_TYPES = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp"
    );

    private final UserRepository userRepository;
    private final AuditService auditService;

    public UserProfileController(UserRepository userRepository, AuditService auditService) {
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @PostMapping(path = "/me/profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ApiResponse<UserSummary> updateOwnProfilePicture(
            @AuthenticationPrincipal SuukaPrincipal principal,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        validateProfileImage(file);
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Path uploadDir = Path.of("uploads", "profile-pictures").toAbsolutePath().normalize();
        Files.createDirectories(uploadDir);

        String extension = extension(file.getOriginalFilename());
        String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        String filename = "profile_user_" + user.getId() + "_" + timestamp + "." + extension;
        Path destination = uploadDir.resolve(filename).normalize();
        if (!destination.startsWith(uploadDir)) {
            throw new IllegalArgumentException("Invalid upload path");
        }

        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        user.setProfilePictureUrl("/uploads/profile-pictures/" + filename);
        auditService.record(user.getId().toString(), "users", "PROFILE_PICTURE_UPDATED", filename);
        return ApiResponse.success("Profile picture updated", UserSummary.from(userRepository.save(user)));
    }

    private void validateProfileImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Profile picture is required");
        }
        if (file.getSize() > MAX_PROFILE_IMAGE_BYTES) {
            throw new IllegalArgumentException("Profile picture must be 5MB or smaller");
        }
        String extension = extension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Only JPG, PNG, or WEBP profile pictures are allowed");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.containsKey(contentType.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("Unsupported image content type");
        }
    }

    private String extension(String originalFilename) {
        String safeName = originalFilename == null ? "" : originalFilename.toLowerCase(Locale.ROOT);
        int index = safeName.lastIndexOf('.');
        if (index < 0 || index == safeName.length() - 1) {
            return "";
        }
        return safeName.substring(index + 1);
    }
}
