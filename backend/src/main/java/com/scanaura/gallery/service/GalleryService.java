package com.scanaura.gallery.service;

import com.scanaura.gallery.dto.GalleryImageResponse;
import com.scanaura.gallery.dto.GalleryReorderRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface GalleryService {

    GalleryImageResponse addImage(
            UUID businessId,
            MultipartFile file
    );

    void deleteImage(
            UUID businessId,
            UUID imageId
    );

    GalleryImageResponse replaceImage(
            UUID businessId,
            UUID imageId,
            MultipartFile file
    );

    void reorderImages(
            UUID businessId,
            GalleryReorderRequest request
    );

    List<GalleryImageResponse> getImages(
            UUID businessId
    );
}