package com.scanaura.publicapi.service.impl;

import com.scanaura.activity.loyalty.entity.LoyaltySettings;
import com.scanaura.activity.loyalty.repository.LoyaltySettingsRepository;
import com.scanaura.business.entity.Business;
import com.scanaura.business.repository.BusinessRepository;
import com.scanaura.catalog.entity.Catalog;
import com.scanaura.catalog.repository.CatalogRepository;
import com.scanaura.category.entity.Category;
import com.scanaura.category.repository.CategoryRepository;
import com.scanaura.common.exception.BusinessException;
import com.scanaura.common.enums.QrType;
import com.scanaura.gallery.dto.GalleryImageResponse;
import com.scanaura.gallery.repository.GalleryImageRepository;
import com.scanaura.publicapi.dto.*;
import com.scanaura.publicapi.service.PublicService;
import com.scanaura.qr.entity.QrCode;
import com.scanaura.qr.repository.QrCodeRepository;
import com.scanaura.qr.service.QrScanService;
import com.scanaura.subscription.service.SubscriptionValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PublicServiceImpl implements PublicService {

    private final QrCodeRepository qrCodeRepository;
    private final BusinessRepository businessRepository;
    private final CategoryRepository categoryRepository;
    private final CatalogRepository catalogRepository;
    private final SubscriptionValidationService subscriptionValidationService;
    private final QrScanService qrScanService;

    private final GalleryImageRepository galleryImageRepository;

    /*
     * Loyalty settings are controlled separately from Business.
     * The businessId stored in LoyaltySettings is used to determine
     * whether loyalty is enabled for the public landing page.
     */
    private final LoyaltySettingsRepository loyaltySettingsRepository;

    @Override
    public LandingResponse getLandingPage(String qrCode) {

        Business business = getBusiness(qrCode);


        subscriptionValidationService.validateBusinessAccess(business);

        // Record one scan for the validated public QR landing visit.
        qrScanService.recordScan(qrCode);

        boolean paymentAvailable =
                business.getUpiId() != null
                        && !business.getUpiId().isBlank()
                        && Boolean.TRUE.equals(
                        business.getPaymentEnabled()
                );

        boolean googleReviewAvailable =
                business.getGoogleReviewUrl() != null
                        && !business.getGoogleReviewUrl().isBlank()
                        && Boolean.TRUE.equals(
                        business.getGoogleReviewEnabled()
                );

        boolean instagramAvailable =
                business.getInstagramUrl() != null
                        && !business.getInstagramUrl().isBlank()
                        && Boolean.TRUE.equals(
                        business.getInstagramEnabled()
                );

        boolean facebookAvailable =
                business.getFacebookUrl() != null
                        && !business.getFacebookUrl().isBlank()
                        && Boolean.TRUE.equals(
                        business.getFacebookEnabled()
                );

        boolean youtubeAvailable =
                business.getYoutubeUrl() != null
                        && !business.getYoutubeUrl().isBlank()
                        && Boolean.TRUE.equals(
                        business.getYoutubeEnabled()
                );

        boolean callAvailable =
                hasValue(business.getPhone())
                        && business.isCallEnabled();

        boolean whatsappAvailable =
                hasValue(business.getWhatsapp())
                        && business.isWhatsappEnabled();

        boolean mapsAvailable =
                hasValue(business.getGoogleMapsUrl())
                        && business.isMapsEnabled();


        boolean galleryAvailable = business.isGalleryEnabled();

        List<GalleryImageResponse> galleryImages =
                galleryAvailable
                        ? galleryImageRepository
                        .findAllByBusinessIdOrderByDisplayOrderAscIdAsc(
                                business.getId()
                        )
                        .stream()
                        .map(image ->
                                GalleryImageResponse.builder()
                                        .id(image.getId())
                                        .imageUrl(image.getImageUrl())
                                        .displayOrder(image.getDisplayOrder())
                                        .build()
                        )
                        .toList()
                        : List.of();

        /*
         * Loyalty is business-owner controlled.
         *
         * If no LoyaltySettings row exists for this business,
         * loyalty is considered disabled.
         */
        boolean loyaltyAvailable =
                loyaltySettingsRepository
                        .findByBusinessId(business.getId())
                        .map(LoyaltySettings::getEnabled)
                        .map(Boolean.TRUE::equals)
                        .orElse(false);

        return LandingResponse.builder()
                .businessId(business.getId())
                .businessName(business.getBusinessName())
                .publicSlug(business.getPublicSlug())
                .businessType(business.getBusinessType())
                .city(business.getCity())
                .logoUrl(business.getLogoUrl())

                // Contact
                .phone(
                        callAvailable
                                ? business.getPhone()
                                : null
                )
                .callEnabled(
                        callAvailable
                )

                .whatsapp(
                        whatsappAvailable
                                ? business.getWhatsapp()
                                : null
                )
                .whatsappEnabled(
                        whatsappAvailable
                )
                .dineInEnabled(business.isDineInEnabled())
                .takeawayEnabled(business.isTakeawayEnabled())
                .deliveryEnabled(business.isDeliveryEnabled())
                .roomServiceEnabled(business.isRoomServiceEnabled())
                .selfServiceEnabled(business.isSelfServiceEnabled())
                .pickupEnabled(business.isPickupEnabled())

                // Location
                .address(business.getAddress())
                .state(business.getState())
                .country(business.getCountry())
                .pincode(business.getPincode())

                .googleMapsUrl(
                        mapsAvailable
                                ? business.getGoogleMapsUrl()
                                : null
                )
                .mapsEnabled(
                        mapsAvailable
                )

                // Gallery
                .galleryEnabled(galleryAvailable)
                .galleryImages(galleryImages)

                .menuAvailable(true)

                .paymentEnabled(paymentAvailable)

                .brandColor(business.getBrandColor())

                .loyaltyEnabled(loyaltyAvailable)

                .googleReviewUrl(
                        googleReviewAvailable
                                ? business.getGoogleReviewUrl()
                                : null
                )
                .googleReviewEnabled(
                        googleReviewAvailable
                )

                .instagramUrl(
                        instagramAvailable
                                ? business.getInstagramUrl()
                                : null
                )
                .instagramEnabled(
                        instagramAvailable
                )

                .facebookUrl(
                        facebookAvailable
                                ? business.getFacebookUrl()
                                : null
                )
                .facebookEnabled(
                        facebookAvailable
                )

                .youtubeUrl(
                        youtubeAvailable
                                ? business.getYoutubeUrl()
                                : null
                )
                .youtubeEnabled(
                        youtubeAvailable
                )

                .build();
    }

    @Override
    public PaymentResponse getPaymentDetails(String qrCode) {

        Business business = getBusiness(qrCode);

        subscriptionValidationService.validateBusinessAccess(business);

        boolean paymentAvailable =
                hasValue(business.getUpiId())
                        && Boolean.TRUE.equals(
                        business.getPaymentEnabled()
                );

        if (!paymentAvailable) {
            throw new BusinessException(
                    "UPI payment is not available."
            );
        }

        return PaymentResponse.builder()
                .businessName(business.getBusinessName())
                .upiId(business.getUpiId())
                .build();
    }

    @Override
    public SharePreviewResponse getSharePreview(String qrCode) {
        return getSharePreview(qrCode, null);
    }

    @Override
    public SharePreviewResponse getSharePreview(String qrCode, String itemId) {
        Business business = getBusiness(qrCode);
        if (itemId == null || itemId.isBlank()) {
            return buildSharePreview(business, false, null);
        }

        final UUID catalogId;
        try {
            catalogId = UUID.fromString(itemId.trim());
        } catch (IllegalArgumentException exception) {
            throw new BusinessException("Menu item not found.");
        }

        Catalog sharedItem = catalogRepository.findByIdAndBusiness(catalogId, business)
                .filter(item -> Boolean.TRUE.equals(item.getActive()))
                .orElseThrow(() -> new BusinessException("Menu item not found."));
        return buildSharePreview(business, false, sharedItem);
    }

    @Override
    public SharePreviewResponse getPublicBusinessPage(String publicSlug) {

        if (publicSlug == null || !publicSlug.matches("^[a-z0-9]+(?:-[a-z0-9]+)*$")) {
            throw new BusinessException("Business page not found.");
        }

        Business business = businessRepository.findByPublicSlugIgnoreCase(publicSlug)
                .filter(value -> Boolean.TRUE.equals(value.getActive()))
                .orElseThrow(() -> new BusinessException("Business page not found."));

        subscriptionValidationService.validateBusinessAccess(business);

        return buildSharePreview(business, true, null);
    }

    @Override
    public String getDigitalQrCodeForPublicSlug(String publicSlug) {
        if (publicSlug == null || !publicSlug.matches("^[a-z0-9]+(?:-[a-z0-9]+)*$")) {
            throw new BusinessException("Business page not found.");
        }
        Business business = businessRepository.findByPublicSlugIgnoreCase(publicSlug)
                .filter(value -> Boolean.TRUE.equals(value.getActive()))
                .orElseThrow(() -> new BusinessException("Business page not found."));
        subscriptionValidationService.validateBusinessAccess(business);
        QrCode qr = qrCodeRepository.findByBusinessAndType(business, QrType.DIGITAL)
                .filter(value -> Boolean.TRUE.equals(value.getActive()))
                .filter(value -> Boolean.TRUE.equals(value.getAssigned()))
                .orElseThrow(() -> new BusinessException("Digital QR not found for this business."));
        return qr.getQrCode();
    }

    @Override
    public List<Business> getBusinessesForSitemap() {
        return businessRepository.findAllByPublicSlugIsNotNullAndActiveTrueOrderByBusinessNameAsc();
    }

    private SharePreviewResponse buildSharePreview(Business business, boolean includeMenu, Catalog sharedItem) {

        String description = business.getDescription();
        if (description == null || description.isBlank()) {
            String location = business.getCity() == null || business.getCity().isBlank()
                    ? ""
                    : " in " + business.getCity().trim();
            description = "Explore " + business.getBusinessName()
                    + location + " — products, services, contact details and more on ScanAura.";
        }

        List<MenuCategoryResponse> menu = includeMenu
                ? buildMenuResponse(business).getMenu()
                : List.of();

        return SharePreviewResponse.builder()
                .businessName(business.getBusinessName())
                .logoUrl(business.getLogoUrl())
                .description(description)
                .businessType(business.getBusinessType() == null ? "LocalBusiness" : business.getBusinessType().name())
                .publicSlug(business.getPublicSlug())
                .itemName(sharedItem == null ? null : sharedItem.getName())
                .itemImageUrl(sharedItem == null ? null : sharedItem.getImageUrl())
                .itemDescription(sharedItem == null ? null : sharedItem.getDescription())
                .phone(business.isCallEnabled() ? business.getPhone() : null)
                .whatsapp(business.getWhatsapp())
                .address(business.getAddress())
                .city(business.getCity())
                .state(business.getState())
                .country(business.getCountry())
                .pincode(business.getPincode())
                .website(business.getWebsite())
                .googleMapsUrl(business.getGoogleMapsUrl())
                .googleReviewUrl(business.getGoogleReviewUrl())
                .instagramUrl(business.getInstagramUrl())
                .facebookUrl(business.getFacebookUrl())
                .youtubeUrl(business.getYoutubeUrl())
                .menu(menu)
                .build();
    }

    @Override
    public MenuResponse getMenu(String qrCode) {

        Business business = getBusiness(qrCode);
        subscriptionValidationService.validateBusinessAccess(business);

        return buildMenuResponse(business);
    }

    private MenuResponse buildMenuResponse(Business business) {

        List<Category> categories =
                categoryRepository
                        .findByBusinessOrderByDisplayOrderAsc(business);

        List<MenuCategoryResponse> menu = new ArrayList<>();

        for (Category category : categories) {

            List<Catalog> catalogs =
                    catalogRepository
                            .findByBusinessAndCategoryOrderByDisplayOrderAsc(
                                    business,
                                    category
                            );

            List<MenuItemResponse> items = catalogs.stream()
                    .filter(catalog -> !Boolean.FALSE.equals(catalog.getAvailable()))
                    .map(catalog -> MenuItemResponse.builder()
                            .id(catalog.getId() == null ? null : catalog.getId().toString())
                            .name(catalog.getName())
                            .description(catalog.getDescription())
                            .price(catalog.getPrice())
                            .imageUrl(catalog.getImageUrl())
                            .veg(catalog.getVeg())
                            .available(catalog.getAvailable())
                            .bestSeller(catalog.getBestSeller())
                            .recommended(catalog.getRecommended())
                            .build())
                    .toList();

            if (!items.isEmpty()) {
                menu.add(MenuCategoryResponse.builder()
                        .categoryName(category.getName())
                        .items(items)
                        .build());
            }
        }

        List<Catalog> uncategorizedItems =
                catalogRepository
                        .findByBusinessAndCategoryIsNullOrderByDisplayOrderAsc(business);

        List<MenuItemResponse> uncategorized = uncategorizedItems.stream()
                .filter(catalog -> !Boolean.FALSE.equals(catalog.getAvailable()))
                .map(catalog -> MenuItemResponse.builder()
                        .id(catalog.getId() == null ? null : catalog.getId().toString())
                        .name(catalog.getName())
                        .description(catalog.getDescription())
                        .price(catalog.getPrice())
                        .imageUrl(catalog.getImageUrl())
                        .veg(catalog.getVeg())
                        .available(catalog.getAvailable())
                        .bestSeller(catalog.getBestSeller())
                        .recommended(catalog.getRecommended())
                        .build())
                .toList();

        if (!uncategorized.isEmpty()) {
            menu.add(MenuCategoryResponse.builder()
                    .categoryName("Other items")
                    .items(uncategorized)
                    .build());
        }

        return MenuResponse.builder()
                .businessName(business.getBusinessName())
                .logoUrl(business.getLogoUrl())
                .menu(menu)
                .build();
    }

    private boolean hasValue(String value) {
        return value != null && !value.isBlank();
    }

    private Business getBusiness(String qrCode) {

        QrCode qr =
                qrCodeRepository
                        .findByQrCode(qrCode)
                        .orElseThrow(() ->
                                new BusinessException(
                                        "QR Code not found."
                                )
                        );

        if (!Boolean.TRUE.equals(qr.getActive())) {
            throw new BusinessException(
                    "QR Code is inactive."
            );
        }

        if (qr.getBusiness() == null) {
            throw new BusinessException(
                    "QR Code is not assigned."
            );
        }

        return qr.getBusiness();
    }
}
