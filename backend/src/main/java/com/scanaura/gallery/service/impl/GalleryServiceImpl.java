package com.scanaura.gallery.service.impl;

import com.scanaura.auth.entity.User;
import com.scanaura.business.entity.Business;
import com.scanaura.business.repository.BusinessRepository;
import com.scanaura.common.enums.ImageType;
import com.scanaura.common.exception.BusinessException;
import com.scanaura.common.util.SecurityUtil;
import com.scanaura.gallery.dto.GalleryImageResponse;
import com.scanaura.gallery.dto.GalleryReorderRequest;
import com.scanaura.gallery.entity.GalleryImage;
import com.scanaura.gallery.repository.GalleryImageRepository;
import com.scanaura.gallery.service.GalleryService;
import com.scanaura.image.dto.ImageUploadResponse;
import com.scanaura.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GalleryServiceImpl implements GalleryService {

    private static final int MAX_GALLERY_IMAGES = 12;

    private final GalleryImageRepository galleryImageRepository;
    private final BusinessRepository businessRepository;
    private final ImageService imageService;

    @Override
    @Transactional
    public GalleryImageResponse addImage(
            UUID businessId,
            MultipartFile file
    ) {

        Business business =
                getOwnedBusiness(businessId);

        long currentCount =
                galleryImageRepository.countByBusinessId(
                        businessId
                );

        if (currentCount >= MAX_GALLERY_IMAGES) {
            throw new BusinessException(
                    "Gallery can contain a maximum of 12 images."
            );
        }

        ImageUploadResponse uploaded =
                imageService.upload(
                        file,
                        ImageType.GALLERY
                );

        try {

            int displayOrder =
                    (int) currentCount;

            GalleryImage galleryImage =
                    GalleryImage.builder()
                            .business(business)
                            .imageUrl(
                                    uploaded.getImageUrl()
                            )
                            .imagePublicId(
                                    uploaded.getPublicId()
                            )
                            .displayOrder(
                                    displayOrder
                            )
                            .build();

            GalleryImage saved =
                    galleryImageRepository.save(
                            galleryImage
                    );

            return mapToResponse(saved);

        } catch (RuntimeException e) {

            /*
             * DB save failed after Cloudinary upload.
             * Remove the newly uploaded image so we
             * don't leave an orphaned Cloudinary asset.
             */
            try {
                imageService.delete(
                        uploaded.getPublicId()
                );
            } catch (Exception ignored) {
                // Do not hide the original exception.
            }

            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteImage(
            UUID businessId,
            UUID imageId
    ) {

        GalleryImage galleryImage =
                getOwnedImage(
                        businessId,
                        imageId
                );

        String publicId =
                galleryImage.getImagePublicId();

        /*
         * Delete the database record first.
         */
        galleryImageRepository.delete(
                galleryImage
        );

        /*
         * Then remove the Cloudinary asset.
         */
        if (publicId != null
                && !publicId.isBlank()) {

            imageService.delete(publicId);
        }

        /*
         * Keep display order continuous:
         * 0, 1, 2, 3...
         */
        normalizeDisplayOrder(businessId);
    }

    @Override
    @Transactional
    public GalleryImageResponse replaceImage(
            UUID businessId,
            UUID imageId,
            MultipartFile file
    ) {

        GalleryImage galleryImage =
                getOwnedImage(
                        businessId,
                        imageId
                );

        String oldPublicId =
                galleryImage.getImagePublicId();

        /*
         * IMPORTANT:
         *
         * Upload the new image FIRST.
         *
         * If upload fails, the old image remains
         * completely untouched.
         */
        ImageUploadResponse uploaded =
                imageService.upload(
                        file,
                        ImageType.GALLERY
                );

        try {

            galleryImage.setImageUrl(
                    uploaded.getImageUrl()
            );

            galleryImage.setImagePublicId(
                    uploaded.getPublicId()
            );

            GalleryImage saved =
                    galleryImageRepository.save(
                            galleryImage
                    );

            /*
             * Only delete the old Cloudinary image
             * after the new image has been saved.
             */
            if (oldPublicId != null
                    && !oldPublicId.isBlank()
                    && !oldPublicId.equals(
                    uploaded.getPublicId()
            )) {

                try {

                    imageService.delete(
                            oldPublicId
                    );

                } catch (Exception ignored) {

                    /*
                     * The database already points to
                     * the new image.
                     *
                     * Do not replace the successful
                     * update just because Cloudinary
                     * cleanup failed.
                     */
                }
            }

            return mapToResponse(saved);

        } catch (RuntimeException e) {

            /*
             * DB update failed after uploading the new
             * Cloudinary image.
             *
             * Remove the new asset and preserve the
             * old image.
             */
            try {

                imageService.delete(
                        uploaded.getPublicId()
                );

            } catch (Exception ignored) {

                // Do not hide the original exception.
            }

            throw e;
        }
    }

    @Override
    @Transactional
    public void reorderImages(
            UUID businessId,
            GalleryReorderRequest request
    ) {

        getOwnedBusiness(businessId);

        List<UUID> imageIds =
                request.getImageIds();

        if (imageIds == null
                || imageIds.isEmpty()) {

            throw new BusinessException(
                    "Image IDs are required."
            );
        }

        /*
         * Prevent duplicate IDs from being submitted.
         */
        if (imageIds.size()
                != imageIds.stream()
                .distinct()
                .count()) {

            throw new BusinessException(
                    "Duplicate gallery image IDs are not allowed."
            );
        }

        List<GalleryImage> images =
                galleryImageRepository
                        .findAllByBusinessIdOrderByDisplayOrderAscIdAsc(
                                businessId
                        );

        /*
         * Every existing image must be included.
         */
        if (images.size()
                != imageIds.size()) {

            throw new BusinessException(
                    "All gallery images must be included when reordering."
            );
        }

        /*
         * Create a lookup map so we don't repeatedly
         * scan the list.
         */
        var imageMap =
                images.stream()
                        .collect(
                                java.util.stream.Collectors.toMap(
                                        GalleryImage::getId,
                                        image -> image
                                )
                        );

        for (int i = 0; i < imageIds.size(); i++) {

            UUID imageId =
                    imageIds.get(i);

            GalleryImage image =
                    imageMap.get(imageId);

            if (image == null) {

                throw new BusinessException(
                        "Invalid gallery image."
                );
            }

            image.setDisplayOrder(i);
        }

        galleryImageRepository.saveAll(images);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GalleryImageResponse> getImages(
            UUID businessId
    ) {

        getOwnedBusiness(businessId);

        return galleryImageRepository
                .findAllByBusinessIdOrderByDisplayOrderAscIdAsc(
                        businessId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Returns the business only when the currently
     * authenticated user owns it.
     */
    private Business getOwnedBusiness(
            UUID businessId
    ) {

        User currentUser =
                SecurityUtil.getCurrentUser();

        return businessRepository
                .findById(businessId)
                .filter(business ->
                        business.getOwner()
                                .getId()
                                .equals(
                                        currentUser.getId()
                                )
                )
                .orElseThrow(() ->
                        new BusinessException(
                                "Business not found."
                        )
                );
    }

    /**
     * Gets a gallery image only when:
     *
     * 1. The business belongs to the current user.
     * 2. The image exists.
     * 3. The image belongs to that business.
     */
    private GalleryImage getOwnedImage(
            UUID businessId,
            UUID imageId
    ) {

        Business business =
                getOwnedBusiness(businessId);

        GalleryImage galleryImage =
                galleryImageRepository
                        .findById(imageId)
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Gallery image not found."
                                )
                        );

        if (!galleryImage
                .getBusiness()
                .getId()
                .equals(
                        business.getId()
                )) {

            throw new BusinessException(
                    "You do not have access to this gallery image."
            );
        }

        return galleryImage;
    }

    /**
     * After deleting an image, normalize the remaining
     * display order to:
     *
     * 0, 1, 2, 3...
     */
    private void normalizeDisplayOrder(
            UUID businessId
    ) {

        List<GalleryImage> images =
                galleryImageRepository
                        .findAllByBusinessIdOrderByDisplayOrderAscIdAsc(
                                businessId
                        );

        for (int i = 0;
             i < images.size();
             i++) {

            images.get(i)
                    .setDisplayOrder(i);
        }

        galleryImageRepository.saveAll(
                images
        );
    }

    private GalleryImageResponse mapToResponse(
            GalleryImage galleryImage
    ) {

        return GalleryImageResponse.builder()
                .id(
                        galleryImage.getId()
                )
                .imageUrl(
                        galleryImage.getImageUrl()
                )
                .displayOrder(
                        galleryImage.getDisplayOrder()
                )
                .build();
    }
}