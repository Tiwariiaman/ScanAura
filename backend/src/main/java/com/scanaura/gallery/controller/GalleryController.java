package com.scanaura.gallery.controller;

import com.scanaura.gallery.dto.GalleryImageResponse;
import com.scanaura.gallery.dto.GalleryReorderRequest;
import com.scanaura.gallery.service.GalleryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/gallery")
@RequiredArgsConstructor
public class GalleryController {

    private final GalleryService galleryService;

    @GetMapping("/{businessId}")
    public ResponseEntity<List<GalleryImageResponse>> getImages(
            @PathVariable UUID businessId
    ) {
        return ResponseEntity.ok(
                galleryService.getImages(businessId)
        );
    }

    @PostMapping("/{businessId}")
    public ResponseEntity<GalleryImageResponse> addImage(
            @PathVariable UUID businessId,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(
                galleryService.addImage(
                        businessId,
                        file
                )
        );
    }

    @PutMapping("/{businessId}/{imageId}")
    public ResponseEntity<GalleryImageResponse> replaceImage(
            @PathVariable UUID businessId,
            @PathVariable UUID imageId,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(
                galleryService.replaceImage(
                        businessId,
                        imageId,
                        file
                )
        );
    }

    @DeleteMapping("/{businessId}/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable UUID businessId,
            @PathVariable UUID imageId
    ) {
        galleryService.deleteImage(
                businessId,
                imageId
        );

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{businessId}/reorder")
    public ResponseEntity<Void> reorderImages(
            @PathVariable UUID businessId,
            @Valid @RequestBody GalleryReorderRequest request
    ) {
        galleryService.reorderImages(
                businessId,
                request
        );

        return ResponseEntity.noContent().build();
    }
}