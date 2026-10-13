package com.campuscart.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class UploadResourceConfig implements WebMvcConfigurer {

    private final String uploadDirectory;

    public UploadResourceConfig(
        @Value("${campuscart.upload-dir:${user.home}/.local/share/campuscart/uploads}")
        String uploadDirectory
    ) {
        this.uploadDirectory = Paths.get(uploadDirectory)
            .toAbsolutePath()
            .normalize()
            .toUri()
            .toString();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = uploadDirectory.endsWith("/")
            ? uploadDirectory
            : uploadDirectory + "/";

        registry.addResourceHandler("/uploads/**")
            .addResourceLocations(location);
    }
}
