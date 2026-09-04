package com.scanaura.publicapi.service.impl;

import com.scanaura.business.entity.Business;
import com.scanaura.catalog.entity.Catalog;
import com.scanaura.catalog.repository.CatalogRepository;
import com.scanaura.category.entity.Category;
import com.scanaura.category.repository.CategoryRepository;
import com.scanaura.common.exception.BusinessException;
import com.scanaura.publicapi.dto.LandingResponse;
import com.scanaura.publicapi.dto.MenuCategoryResponse;
import com.scanaura.publicapi.dto.MenuItemResponse;
import com.scanaura.publicapi.dto.MenuResponse;
import com.scanaura.publicapi.dto.PaymentResponse;
import com.scanaura.publicapi.service.PublicService;
import com.scanaura.qr.entity.QrCode;
import com.scanaura.qr.repository.QrCodeRepository;
import com.scanaura.subscription.service.SubscriptionValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicServiceImpl implements PublicService {

    private final QrCodeRepository qrCodeRepository;
    private final CategoryRepository categoryRepository;
    private final CatalogRepository catalogRepository;
    private final SubscriptionValidationService subscriptionValidationService;

    @Override
    public LandingResponse getLandingPage(String qrCode) {

        Business business = getBusiness(qrCode);

        subscriptionValidationService.validateBusinessAccess(business);

        boolean paymentAvailable =
                hasValue(business.getUpiId())
                        && Boolean.TRUE.equals(business.getPaymentEnabled());

        boolean googleReviewAvailable =
                hasValue(business.getGoogleReviewUrl())
                        && Boolean.TRUE.equals(business.getGoogleReviewEnabled());

        return LandingResponse.builder()
                .businessName(business.getBusinessName())
                .businessType(business.getBusinessType())
                .city(business.getCity())
                .logoUrl(business.getLogoUrl())
                .menuAvailable(true)
                .paymentEnabled(paymentAvailable)
                .googleReviewUrl(
                        googleReviewAvailable
                                ? business.getGoogleReviewUrl()
                                : null
                )
                .googleReviewEnabled(googleReviewAvailable)
                .build();
    }

    @Override
    public PaymentResponse getPaymentDetails(String qrCode) {

        Business business = getBusiness(qrCode);

        subscriptionValidationService.validateBusinessAccess(business);

        boolean paymentAvailable =
                hasValue(business.getUpiId())
                        && Boolean.TRUE.equals(business.getPaymentEnabled());

        if (!paymentAvailable) {
            throw new BusinessException("UPI payment is not available.");
        }

        return PaymentResponse.builder()
                .businessName(business.getBusinessName())
                .upiId(business.getUpiId())
                .build();
    }

    @Override
    public MenuResponse getMenu(String qrCode) {

        Business business = getBusiness(qrCode);

        subscriptionValidationService.validateBusinessAccess(business);

        List<Category> categories =
                categoryRepository.findByBusinessOrderByDisplayOrderAsc(business);

        List<MenuCategoryResponse> menu = new ArrayList<>();

        for (Category category : categories) {

            List<Catalog> catalogs =
                    catalogRepository
                            .findByBusinessAndCategoryOrderByDisplayOrderAsc(
                                    business,
                                    category
                            );

            List<MenuItemResponse> items = catalogs.stream()
                    .map(catalog -> MenuItemResponse.builder()
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

            menu.add(
                    MenuCategoryResponse.builder()
                            .categoryName(category.getName())
                            .items(items)
                            .build()
            );
        }

        List<Catalog> uncategorizedItems =
                catalogRepository.findByBusinessAndCategoryIsNullOrderByDisplayOrderAsc(
                        business
                );

        if (!uncategorizedItems.isEmpty()) {

            List<MenuItemResponse> items = uncategorizedItems.stream()
                    .map(catalog -> MenuItemResponse.builder()
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

            menu.add(
                    MenuCategoryResponse.builder()
                            .categoryName(null)
                            .items(items)
                            .build()
            );
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

        QrCode qr = qrCodeRepository.findByQrCode(qrCode)
                .orElseThrow(() ->
                        new BusinessException("QR Code not found.")
                );

        if (!Boolean.TRUE.equals(qr.getActive())) {
            throw new BusinessException("QR Code is inactive.");
        }

        if (qr.getBusiness() == null) {
            throw new BusinessException("QR Code is not assigned.");
        }

        return qr.getBusiness();
    }
}