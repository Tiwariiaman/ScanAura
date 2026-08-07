package com.scanaura.image.service;

import com.scanaura.common.enums.ImageType;
import com.scanaura.image.dto.ImageUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    ImageUploadResponse upload(
            MultipartFile file,
            ImageType type
    );

    void delete(String publicId);

}