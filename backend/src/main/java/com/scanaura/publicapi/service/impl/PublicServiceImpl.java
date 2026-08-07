package com.scanaura.publicapi.service.impl;

import com.scanaura.business.entity.Business;
import com.scanaura.catalog.entity.Catalog;
import com.scanaura.catalog.repository.CatalogRepository;
import com.scanaura.category.entity.Category;
import com.scanaura.category.repository.CategoryRepository;
import com.scanaura.common.exception.BusinessException;
import com.scanaura.publicapi.dto.*;
import com.scanaura.publicapi.service.PublicService;
import com.scanaura.qr.entity.QrCode;
import com.scanaura.qr.repository.QrCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PublicServiceImpl implements PublicService {

    private final QrCodeRepository qrCodeRepository;
    private final CategoryRepository categoryRepository;
    private final CatalogRepository catalogRepository;

    @Override
    public LandingResponse getLandingPage(String qrCode) {

        Business business = getBusiness(qrCode);

        return LandingResponse.builder()
                .businessName(business.getBusinessName())
                .businessType(business.getBusinessType())
                .city(business.getCity())
                .logoUrl(business.getLogoUrl())
                .menuAvailable(true)
                .paymentEnabled(
                        business.getUpiId() != null &&
                                !business.getUpiId().isBlank()
                )
                .build();
    }

    @Override
    public PaymentResponse getPaymentDetails(String qrCode) {

        Business business = getBusiness(qrCode);

        return PaymentResponse.builder()
                .businessName(business.getBusinessName())
                .upiId(business.getUpiId())
                .build();
    }

    @Override
    public MenuResponse getMenu(String qrCode) {

        Business business = getBusiness(qrCode);

        List<Category> categories =
                categoryRepository.findByBusinessOrderByDisplayOrderAsc(business);

        List<MenuCategoryResponse> menu = new ArrayList<>();

        for (Category category : categories) {

            List<Catalog> catalogs =
                    catalogRepository.findByBusinessAndCategoryOrderByDisplayOrderAsc(
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



    private Business getBusiness(String qrCode) {

        QrCode qr = qrCodeRepository.findByQrCode(qrCode)
                .orElseThrow(() ->
                        new BusinessException("QR Code not found."));

        if (!qr.getActive()) {
            throw new BusinessException("QR Code is inactive.");
        }

        if (qr.getBusiness() == null) {
            throw new BusinessException("QR Code is not assigned.");
        }

        return qr.getBusiness();
    }
}
