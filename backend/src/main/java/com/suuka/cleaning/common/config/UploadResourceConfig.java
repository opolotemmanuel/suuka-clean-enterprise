package com.suuka.cleaning.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class UploadResourceConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path profileUploadPath = Path.of("uploads", "profile-pictures").toAbsolutePath().normalize();
        registry.addResourceHandler("/uploads/profile-pictures/**")
                .addResourceLocations(profileUploadPath.toUri().toString() + "/");
    }
}
