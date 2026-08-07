package com.scanaura.image.controller;

import com.scanaura.common.enums.ImageType;
import com.scanaura.common.response.ApiResponse;
import com.scanaura.image.dto.DeleteImageRequest;
import com.scanaura.image.dto.ImageUploadResponse;
import com.scanaura.image.service.ImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<ImageUploadResponse>> uploadImage(
            @RequestParam MultipartFile file,
            @RequestParam ImageType type
    ) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Image uploaded successfully.",
                        imageService.upload(file, type)
                )
        );
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> deleteImage(
            @Valid @RequestBody DeleteImageRequest request
    ) {

        imageService.delete(request.getPublicId());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Image deleted successfully.",
                        "Success"
                )
        );
    }


}
