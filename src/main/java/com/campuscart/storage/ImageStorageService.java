package com.campuscart.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageStorageService {

    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;

    private static final Set<String> ALLOWED_CONTENT_TYPES =
        Set.of("image/jpeg", "image/png");

    private final Path uploadDirectory;

    public ImageStorageService(
        @Value("${campuscart.upload-dir:${user.home}/.local/share/campuscart/uploads}")
        String uploadDirectory
    ) {
        this.uploadDirectory = Paths.get(uploadDirectory)
            .toAbsolutePath()
            .normalize();

        try {
            Files.createDirectories(this.uploadDirectory);
        } catch (IOException exception) {
            throw new IllegalStateException(
                "The image upload directory could not be created.",
                exception
            );
        }
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                "The image must be 5 MB or smaller."
            );
        }

        String contentType = file.getContentType();

        if (contentType == null ||
            !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException(
                "Please upload a JPEG or PNG image."
            );
        }

        String extension = switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            default -> throw new IllegalArgumentException(
                "This image format is not supported."
            );
        };

        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage image = ImageIO.read(inputStream);

            if (image == null) {
                throw new IllegalArgumentException(
                    "The uploaded file is not a valid image."
                );
            }
        } catch (IOException exception) {
            throw new IllegalArgumentException(
                "The uploaded image could not be read."
            );
        }

        String filename = UUID.randomUUID() + extension;
        Path destination = uploadDirectory.resolve(filename).normalize();

        if (!destination.getParent().equals(uploadDirectory)) {
            throw new IllegalArgumentException("Invalid image destination.");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + filename;
        } catch (IOException exception) {
            throw new IllegalStateException(
                "The uploaded image could not be saved.",
                exception
            );
        }
    }
}
