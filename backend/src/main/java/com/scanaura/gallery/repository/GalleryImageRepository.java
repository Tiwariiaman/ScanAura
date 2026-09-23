package com.scanaura.gallery.repository;

import com.scanaura.gallery.entity.GalleryImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GalleryImageRepository extends JpaRepository<GalleryImage, UUID> {

    List<GalleryImage> findAllByBusinessIdOrderByDisplayOrderAscIdAsc(
            UUID businessId
    );

    long countByBusinessId(UUID businessId);
}