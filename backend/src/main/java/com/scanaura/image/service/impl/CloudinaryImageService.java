package com.scanaura.image.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.scanaura.common.constants.ImageFolder;
import com.scanaura.common.enums.ImageType;
import com.scanaura.common.exception.BusinessException;
import com.scanaura.image.dto.ImageUploadResponse;
import com.scanaura.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CloudinaryImageService implements ImageService {

    private final Cloudinary cloudinary;

    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2 MB

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp"
    );

    @Override
    public ImageUploadResponse upload(MultipartFile file, ImageType type) {

        validate(file);

        try {

            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", getFolder(type),
                            "resource_type", "image",
                            "quality", "auto",
                            "fetch_format", "auto"
                    )
            );

            return ImageUploadResponse.builder()
                    .imageUrl(result.get("secure_url").toString())
                    .publicId(result.get("public_id").toString())
                    .build();

        } catch (IOException e) {

            throw new BusinessException("Failed to upload image.");

        }
    }

    @Override
    public void delete(String publicId) {

        try {

            cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.emptyMap()
            );

        } catch (IOException e) {

            throw new BusinessException("Failed to delete image.");

        }

    }

    private void validate(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BusinessException("Image is required.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("Maximum image size is 2 MB.");
        }

        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new BusinessException(
                    "Only JPG, JPEG, PNG and WEBP images are allowed."
            );
        }
    }

    private String getFolder(ImageType type) {

        return switch (type) {

            case BUSINESS -> ImageFolder.BUSINESS;

            case CATALOG -> ImageFolder.CATALOG;

            case PAYMENT_SCREENSHOT ->
                    ImageFolder.PAYMENT_SCREENSHOT;
        };
    }

}