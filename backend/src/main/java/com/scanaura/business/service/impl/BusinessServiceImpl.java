package com.scanaura.business.service.impl;

import com.scanaura.auth.entity.User;
import com.scanaura.business.dto.BusinessRequest;
import com.scanaura.business.dto.BusinessResponse;
import com.scanaura.business.entity.Business;
import com.scanaura.business.repository.BusinessRepository;
import com.scanaura.business.service.BusinessService;
import com.scanaura.common.exception.BusinessException;
import com.scanaura.common.util.SecurityUtil;
import com.scanaura.image.service.ImageService;
import com.scanaura.qr.service.QrService;
import com.scanaura.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BusinessServiceImpl implements BusinessService {

    private final BusinessRepository businessRepository;
    private final QrService qrService;
    private final SubscriptionService subscriptionService;
    private final ImageService imageService;

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

        business.setPhone(
                request.getPhone()
        );

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
                .whatsapp(
                        business.getWhatsapp()
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
}