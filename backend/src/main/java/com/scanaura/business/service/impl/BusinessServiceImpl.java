    package com.scanaura.business.service.impl;

    import com.scanaura.auth.entity.User;
    import com.scanaura.business.dto.BusinessDashboardResponse;
    import com.scanaura.business.dto.BusinessRequest;
    import com.scanaura.business.dto.BusinessResponse;
    import com.scanaura.business.entity.Business;
    import com.scanaura.business.repository.BusinessRepository;
    import com.scanaura.business.service.BusinessService;
    import com.scanaura.common.exception.BusinessException;
    import com.scanaura.common.util.SecurityUtil;
    import com.scanaura.image.service.ImageService;
    import com.scanaura.qr.repository.QrScanDailyRepository;
    import com.scanaura.qr.service.QrService;
    import com.scanaura.subscription.entity.Subscription;
    import com.scanaura.subscription.repository.SubscriptionRepository;
    import com.scanaura.subscription.service.SubscriptionService;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.time.LocalDate;
    import java.util.UUID;

    @Service
    @RequiredArgsConstructor
    public class BusinessServiceImpl implements BusinessService {

        private final BusinessRepository businessRepository;
        private final QrService qrService;
        private final SubscriptionService subscriptionService;
        private final ImageService imageService;

        private final SubscriptionRepository subscriptionRepository;
        private final QrScanDailyRepository qrScanDailyRepository;

        @Override
        @Transactional
        public BusinessResponse createBusiness(
                BusinessRequest request
        ) {

            User currentUser = SecurityUtil.getCurrentUser();

            if (businessRepository.existsByOwner(currentUser)) {
                throw new BusinessException(
                        "Business already exists."
                );
            }

            Business business = new Business();

            business.setOwner(currentUser);

            business.setBusinessName(
                    request.getBusinessName()
            );

            business.setBusinessType(
                    request.getBusinessType()
            );

            // Default supported services on for new businesses; owners can turn them off.
            business.setDineInEnabled(request.getDineInEnabled() == null || request.getDineInEnabled());
            business.setTakeawayEnabled(request.getTakeawayEnabled() == null || request.getTakeawayEnabled());
            business.setDeliveryEnabled(request.getDeliveryEnabled() == null || request.getDeliveryEnabled());
            business.setRoomServiceEnabled(request.getRoomServiceEnabled() == null || request.getRoomServiceEnabled());
            business.setSelfServiceEnabled(request.getSelfServiceEnabled() == null || request.getSelfServiceEnabled());
            business.setPickupEnabled(request.getPickupEnabled() == null || request.getPickupEnabled());

            business.setPhone(
                    request.getPhone()
            );

            business.setBrandColor(normalizeBrandColor(request.getBrandColor()));

            business.setLogoUrl(
                    request.getLogoUrl()
            );

            business.setLogoPublicId(
                    request.getLogoPublicId()
            );

            business.setWhatsapp(
                    request.getWhatsapp()
            );

            business.setEmail(
                    request.getEmail()
            );

            business.setAddress(
                    request.getAddress()
            );

            business.setCity(
                    request.getCity()
            );

            business.setState(
                    request.getState()
            );

            business.setCountry(
                    request.getCountry()
            );

            business.setPincode(
                    request.getPincode()
            );

            business.setWebsite(
                    request.getWebsite()
            );

            business.setDescription(
                    request.getDescription()
            );

            business.setUpiId(
                    request.getUpiId()
            );

            business.setGoogleReviewUrl(
                    request.getGoogleReviewUrl()
            );

            business.setGoogleReviewEnabled(
                    Boolean.TRUE.equals(
                            request.getGoogleReviewEnabled()
                    )
            );

            business.setPaymentEnabled(
                    request.getPaymentEnabled() == null
                            ? true
                            : request.getPaymentEnabled()
            );

            business.setQrSlug(
                    UUID.randomUUID().toString()
            );
            String publicSlug = normalizePublicSlug(request.getPublicSlug());

            if (publicSlug == null) {
                publicSlug = generateUniquePublicSlug(request.getBusinessName());
            } else if (businessRepository.existsByPublicSlugIgnoreCase(publicSlug)) {
                throw new BusinessException("This business link is already taken.");
            }

            business.setPublicSlug(publicSlug);

            business.setInstagramUrl(request.getInstagramUrl());
            business.setInstagramEnabled(
                    Boolean.TRUE.equals(request.getInstagramEnabled())
            );

            business.setFacebookUrl(request.getFacebookUrl());
            business.setFacebookEnabled(
                    Boolean.TRUE.equals(request.getFacebookEnabled())
            );

            business.setYoutubeUrl(request.getYoutubeUrl());
            business.setYoutubeEnabled(
                    Boolean.TRUE.equals(request.getYoutubeEnabled())
            );

            business.setGoogleMapsUrl(
                    request.getGoogleMapsUrl()
            );

            business.setCallEnabled(
                    Boolean.TRUE.equals(request.getCallEnabled())
            );

            business.setWhatsappEnabled(
                    Boolean.TRUE.equals(request.getWhatsappEnabled())
            );

            business.setMapsEnabled(
                    Boolean.TRUE.equals(request.getMapsEnabled())
            );

            business.setGalleryEnabled(
                    Boolean.TRUE.equals(request.getGalleryEnabled())
            );

            business.setActive(true);

            Business savedBusiness =
                    businessRepository.save(business);

            // Create 7-day Trial subscription.
            subscriptionService.createTrialSubscription(
                    savedBusiness
            );

            // Generate default digital QR.
            qrService.generateDigitalQr(
                    savedBusiness.getId()
            );

            return mapToResponse(savedBusiness);
        }

        @Override
        public BusinessResponse getMyBusiness() {

            User currentUser =
                    SecurityUtil.getCurrentUser();

            Business business =
                    businessRepository.findByOwner(currentUser)
                            .orElseThrow(() ->
                                    new BusinessException(
                                            "Business not found."
                                    ));

            return mapToResponse(business);
        }

        @Override
        @Transactional
        public BusinessResponse updateBusiness(
                BusinessRequest request
        ) {

            User currentUser =
                    SecurityUtil.getCurrentUser();

            Business business =
                    businessRepository.findByOwner(currentUser)
                            .orElseThrow(() ->
                                    new BusinessException(
                                            "Business not found."
                                    ));

            String oldLogoPublicId =
                    business.getLogoPublicId();

            /*
             * Update the logo only when a new Cloudinary
             * public ID is supplied.
             *
             * This prevents normal business updates from
             * accidentally removing the existing logo.
             */
            if (request.getLogoPublicId() != null
                    && !request.getLogoPublicId()
                    .trim()
                    .isEmpty()) {

                String newLogoPublicId =
                        request.getLogoPublicId().trim();

                business.setLogoUrl(
                        request.getLogoUrl()
                );

                business.setLogoPublicId(
                        newLogoPublicId
                );

                /*
                 * Delete the old Cloudinary image only when
                 * it is different from the new image.
                 */
                if (oldLogoPublicId != null
                        && !oldLogoPublicId.trim().isEmpty()
                        && !oldLogoPublicId
                        .trim()
                        .equals(newLogoPublicId)) {

                    imageService.delete(
                            oldLogoPublicId.trim()
                    );
                }

            } else if (request.getLogoUrl() != null
                    && !request.getLogoUrl().trim().isEmpty()) {

                /*
                 * Backward compatibility:
                 * keep the old public ID when an older
                 * frontend sends only logoUrl.
                 */
                business.setLogoUrl(
                        request.getLogoUrl()
                );
            }

            business.setBusinessName(
                    request.getBusinessName()
            );

            business.setBusinessType(
                    request.getBusinessType()
            );

            business.setPhone(
                    request.getPhone()
            );

            // Generic profile/order updates may omit appearance settings.
            // Keep the saved brand colour unless the appearance UI explicitly sends one.
            if (request.getBrandColor() != null) {
                business.setBrandColor(normalizeBrandColor(request.getBrandColor()));
            }

            business.setWhatsapp(
                    request.getWhatsapp()
            );

            business.setEmail(
                    request.getEmail()
            );

            business.setAddress(
                    request.getAddress()
            );

            business.setCity(
                    request.getCity()
            );

            business.setState(
                    request.getState()
            );

            business.setCountry(
                    request.getCountry()
            );

            business.setPincode(
                    request.getPincode()
            );

            business.setWebsite(
                    request.getWebsite()
            );

            business.setDescription(
                    request.getDescription()
            );

            business.setUpiId(
                    request.getUpiId()
            );

            business.setGoogleReviewUrl(
                    request.getGoogleReviewUrl()
            );

            if (business.getPublicSlug() == null || business.getPublicSlug().isBlank()) {

                String publicSlug = normalizePublicSlug(request.getPublicSlug());

                if (publicSlug == null) {
                    publicSlug = generateUniquePublicSlug(request.getBusinessName());
                } else if (businessRepository.existsByPublicSlugIgnoreCase(publicSlug)) {
                    throw new BusinessException("This business link is already taken.");
                }

                business.setPublicSlug(publicSlug);
            }

            /*
             * Only update feature toggles when the request
             * actually provides them.
             */
            if (request.getGoogleReviewEnabled() != null) {
                business.setGoogleReviewEnabled(
                        request.getGoogleReviewEnabled()
                );
            }

            if (request.getPaymentEnabled() != null) {
                business.setPaymentEnabled(
                        request.getPaymentEnabled()
                );
            }

            if (request.getInstagramUrl() != null) {
                business.setInstagramUrl(request.getInstagramUrl());
            }

            if (request.getInstagramEnabled() != null) {
                business.setInstagramEnabled(
                        request.getInstagramEnabled()
                );
            }

            if (request.getFacebookUrl() != null) {
                business.setFacebookUrl(request.getFacebookUrl());
            }

            if (request.getFacebookEnabled() != null) {
                business.setFacebookEnabled(
                        request.getFacebookEnabled()
                );
            }

            if (request.getYoutubeUrl() != null) {
                business.setYoutubeUrl(request.getYoutubeUrl());
            }

            if (request.getYoutubeEnabled() != null) {
                business.setYoutubeEnabled(
                        request.getYoutubeEnabled()
                );
            }

            if (request.getGoogleMapsUrl() != null) {
                business.setGoogleMapsUrl(
                        request.getGoogleMapsUrl()
                );
            }

            if (request.getCallEnabled() != null) {
                business.setCallEnabled(request.getCallEnabled());
            }

            if (request.getWhatsappEnabled() != null) {
                business.setWhatsappEnabled(request.getWhatsappEnabled());
            }

            if (request.getMapsEnabled() != null) {
                business.setMapsEnabled(request.getMapsEnabled());
            }

            if (request.getGalleryEnabled() != null) {
                business.setGalleryEnabled(request.getGalleryEnabled());
            }

            if (request.getDineInEnabled() != null) business.setDineInEnabled(request.getDineInEnabled());
            if (request.getTakeawayEnabled() != null) business.setTakeawayEnabled(request.getTakeawayEnabled());
            if (request.getDeliveryEnabled() != null) business.setDeliveryEnabled(request.getDeliveryEnabled());
            if (request.getRoomServiceEnabled() != null) business.setRoomServiceEnabled(request.getRoomServiceEnabled());
            if (request.getSelfServiceEnabled() != null) business.setSelfServiceEnabled(request.getSelfServiceEnabled());
            if (request.getPickupEnabled() != null) business.setPickupEnabled(request.getPickupEnabled());

            Business updatedBusiness =
                    businessRepository.save(business);

            return mapToResponse(updatedBusiness);
        }

        @Override
        @Transactional
        public void deleteBusiness() {

            User currentUser =
                    SecurityUtil.getCurrentUser();

            Business business =
                    businessRepository.findByOwner(currentUser)
                            .orElseThrow(() ->
                                    new BusinessException(
                                            "Business not found."
                                    ));

            String logoPublicId =
                    business.getLogoPublicId();

            businessRepository.delete(business);

            /*
             * Remove the business logo from Cloudinary
             * after the business has been deleted.
             */
            if (logoPublicId != null
                    && !logoPublicId.trim().isEmpty()) {

                imageService.delete(
                        logoPublicId.trim()
                );
            }
        }

        private BusinessResponse mapToResponse(
                Business business
        ) {

            return BusinessResponse.builder()
                    .id(
                            business.getId()
                    )
                    .businessName(
                            business.getBusinessName()
                    )
                    .businessType(
                            business.getBusinessType()
                    )
                    .logoUrl(
                            business.getLogoUrl()
                    )
                    .phone(
                            business.getPhone()
                    )
                    .callEnabled(
                            business.isCallEnabled()
                    )
                    .brandColor(
                            business.getBrandColor()
                    )
                    .whatsapp(
                            business.getWhatsapp()
                    )
                    .whatsappEnabled(
                            business.isWhatsappEnabled()
                    )
                    .email(
                            business.getEmail()
                    )
                    .address(
                            business.getAddress()
                    )
                    .city(
                            business.getCity()
                    )
                    .state(
                            business.getState()
                    )
                    .country(
                            business.getCountry()
                    )
                    .pincode(
                            business.getPincode()
                    )
                    .googleMapsUrl(
                            business.getGoogleMapsUrl()
                    )
                    .mapsEnabled(
                            business.isMapsEnabled()
                    )
                    .galleryEnabled(
                            business.isGalleryEnabled()
                    )
                    .website(
                            business.getWebsite()
                    )
                    .description(
                            business.getDescription()
                    )
                    .upiId(
                            business.getUpiId()
                    )
                    .googleReviewUrl(
                            business.getGoogleReviewUrl()
                    )
                    .googleReviewEnabled(
                            business.getGoogleReviewEnabled()
                    )
                    .paymentEnabled(
                            business.getPaymentEnabled()
                    )
                    .qrSlug(
                            business.getQrSlug()
                    )
                    .publicSlug(
                            business.getPublicSlug()
                    )
                    .dineInEnabled(business.isDineInEnabled())
                    .takeawayEnabled(business.isTakeawayEnabled())
                    .deliveryEnabled(business.isDeliveryEnabled())
                    .roomServiceEnabled(business.isRoomServiceEnabled())
                    .selfServiceEnabled(business.isSelfServiceEnabled())
                    .pickupEnabled(business.isPickupEnabled())
                    .instagramUrl(
                            business.getInstagramUrl()
                    )
                    .instagramEnabled(
                            business.getInstagramEnabled()
                    )
                    .facebookUrl(
                            business.getFacebookUrl()
                    )
                    .facebookEnabled(
                            business.getFacebookEnabled()
                    )
                    .youtubeUrl(
                            business.getYoutubeUrl()
                    )
                    .youtubeEnabled(
                            business.getYoutubeEnabled()
                    )
                    .active(
                            business.getActive()
                    )
                    .build();
        }



        private String normalizeBrandColor(String value) {
            if (value == null) {
                return null;
            }

            String color = value.trim();

            if (color.isEmpty()) {
                return null;
            }

            if (!color.startsWith("#")) {
                color = "#" + color;
            }

            return color.toUpperCase();
        }

        private String normalizePublicSlug(String value) {

            if (value == null || value.trim().isEmpty()) {
                return null;
            }

            String slug = value.trim().toLowerCase(java.util.Locale.ROOT);

            if (!slug.matches("^[a-z0-9]+(?:-[a-z0-9]+)*$") || slug.length() > 100) {
                throw new BusinessException(
                        "Business link can contain only lowercase letters, numbers and single hyphens."
                );
            }
            if (isReservedPublicSlug(slug)) {
                throw new BusinessException("This business link is reserved.");
            }

            return slug;
        }

        private String generateUniquePublicSlug(String businessName) {

            String base = businessName == null ? "" : businessName
                    .trim()
                    .toLowerCase(java.util.Locale.ROOT)
                    .replaceAll("[^a-z0-9]+", "-")
                    .replaceAll("^-|-$", "");

            if (base.isBlank()) {
                base = "business";
            }

            if (base.length() > 70) {
                base = base.substring(0, 70).replaceAll("-+$", "");
            }

            String candidate = base;
            int suffix = 2;

            while (isReservedPublicSlug(candidate)
                    || businessRepository.existsByPublicSlugIgnoreCase(candidate)) {
                String ending = "-" + suffix++;
                int maxBaseLength = 90 - ending.length();
                String shortened = base.length() > maxBaseLength
                        ? base.substring(0, maxBaseLength).replaceAll("-+$", "")
                        : base;
                candidate = shortened + ending;
            }

            return candidate;
        }

        private boolean isReservedPublicSlug(String slug) {
            return java.util.Set.of(
                    "www", "api", "admin", "app", "mail", "support", "help",
                    "blog", "docs", "dashboard", "login", "register", "signup",
                    "pricing", "about", "contact", "status", "cdn", "static",
                    "business"
            ).contains(slug);
        }

        @Override
        public BusinessDashboardResponse getMyDashboard() {

            User currentUser =
                    SecurityUtil.getCurrentUser();

            Business business =
                    businessRepository.findByOwner(currentUser)
                            .orElseThrow(() ->
                                    new BusinessException(
                                            "Business not found."
                                    ));

            Subscription subscription =
                    subscriptionRepository
                            .findByBusiness(business)
                            .orElse(null);

            LocalDate today = LocalDate.now();

            LocalDate yesterday =
                    today.minusDays(1);

            LocalDate last7DaysStart =
                    today.minusDays(6);

            long todayScans =
                    qrScanDailyRepository
                            .getTotalScansByBusinessAndDate(
                                    business,
                                    today
                            );

            long yesterdayScans =
                    qrScanDailyRepository
                            .getTotalScansByBusinessAndDate(
                                    business,
                                    yesterday
                            );

            long last7DaysScans =
                    qrScanDailyRepository
                            .getTotalScansByBusinessAndDateRange(
                                    business,
                                    last7DaysStart,
                                    today
                            );

            long totalScans =
                    qrScanDailyRepository
                            .getTotalScansByBusiness(
                                    business
                            );

            return BusinessDashboardResponse.builder()

                    .businessId(
                            business.getId()
                    )

                    .businessName(
                            business.getBusinessName()
                    )

                    .ownerName(
                            business.getOwner().getFullName()
                    )

                    .email(
                            business.getEmail()
                    )

                    .phone(
                            business.getPhone()
                    )

                    .city(
                            business.getCity()
                    )

                    .active(
                            business.getActive()
                    )

                    .subscriptionStatus(
                            subscription != null
                                    ? subscription.getStatus()
                                    : null
                    )

                    .currentPlan(
                            subscription != null
                                    ? subscription.getPlan().getName()
                                    : null
                    )

                    .todayScans(
                            todayScans
                    )

                    .yesterdayScans(
                            yesterdayScans
                    )

                    .last7DaysScans(
                            last7DaysScans
                    )

                    .totalScans(
                            totalScans
                    )

                    .build();
        }


        @Override
        @Transactional(readOnly = true)
        public boolean isPublicSlugAvailable(String slug) {
            if (slug == null) {
                return false;
            }

            String normalized = slug.trim().toLowerCase(
                    java.util.Locale.ROOT
            );

            if (!normalized.matches(
                    "^[a-z0-9]+(?:-[a-z0-9]+)*$")) {
                return false;
            }

            User currentUser = SecurityUtil.getCurrentUser();

            return businessRepository.findByPublicSlugIgnoreCase(normalized)
                    .map(existing ->
                            existing.getOwner().getId()
                                    .equals(currentUser.getId()))
                    .orElse(true);
        }

    }