package com.scanaura.gallery.entity;

import com.scanaura.business.entity.Business;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "gallery_images",
        indexes = {
                @Index(
                        name = "idx_gallery_images_business_id",
                        columnList = "business_id"
                ),
                @Index(
                        name = "idx_gallery_images_business_order",
                        columnList = "business_id, display_order"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GalleryImage {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "business_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_gallery_images_business"
            )
    )
    private Business business;

    @Column(name = "image_url", nullable = false, length = 1000)
    private String imageUrl;

    @Column(name = "image_public_id", nullable = false, length = 500)
    private String imagePublicId;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (displayOrder == null) {
            displayOrder = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}