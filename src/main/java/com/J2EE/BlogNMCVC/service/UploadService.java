package com.J2EE.BlogNMCVC.service;

import com.J2EE.BlogNMCVC.dto.response.UploadResponse;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class UploadService {

    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif"
    );

    @Autowired
    private Cloudinary cloudinary;

    public UploadResponse uploadImage(MultipartFile file, String folder) {
        validateImage(file);

        String publicId = buildPublicId(folder);

        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "resource_type", "image",
                            "overwrite", true
                    )
            );

            return UploadResponse.builder()
                    .publicId((String) result.get("public_id"))
                    .secureUrl((String) result.get("secure_url"))
                    .format((String) result.get("format"))
                    .bytes(result.get("bytes") == null ? null : Long.valueOf(result.get("bytes").toString()))
                    .build();

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    public void deleteImage(String publicId) {
        try {
            cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap("resource_type", "image")
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete image", e);
        }
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file must not be empty");
        }

        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Only jpg, png, webp and gif images are allowed");
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("Image size must not exceed 10MB");
        }
    }

    private String buildPublicId(String folder) {
        if (folder == null || folder.isBlank()) {
            throw new IllegalArgumentException("Folder must not be blank");
        }

        return folder + "/" + UUID.randomUUID();
    }
}