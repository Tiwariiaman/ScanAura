package com.scanaura.publicapi.controller;

import com.scanaura.common.response.ApiResponse;
import com.scanaura.publicapi.dto.LandingResponse;
import com.scanaura.publicapi.dto.MenuResponse;
import com.scanaura.publicapi.dto.PaymentResponse;
import com.scanaura.publicapi.dto.SharePreviewResponse;
import com.scanaura.publicapi.service.PublicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final PublicService publicService;

    @Value("${scanaura.public-domain:scanaura.in}")
    private String publicDomain;

    @GetMapping("/q/{qrCode}")
    public ResponseEntity<ApiResponse<LandingResponse>> landing(
            @PathVariable String qrCode) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Landing page fetched successfully.",
                        publicService.getLandingPage(qrCode)
                )
        );
    }

    @GetMapping("/q/{qrCode}/menu")
    public ResponseEntity<ApiResponse<MenuResponse>> menu(
            @PathVariable String qrCode) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Menu fetched successfully.",
                        publicService.getMenu(qrCode)
                )
        );
    }

    @GetMapping("/q/{qrCode}/payment")
    public ResponseEntity<ApiResponse<PaymentResponse>> payment(
            @PathVariable String qrCode) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Payment details fetched successfully.",
                        publicService.getPaymentDetails(qrCode)
                )
        );
    }

    @GetMapping("/share/{qrCode}")
    public ModelAndView sharePreview(
            @PathVariable String qrCode) {

        SharePreviewResponse preview =
                publicService.getSharePreview(qrCode);

        ModelAndView modelAndView =
                new ModelAndView("public/share-preview");

        modelAndView.addObject(
                "businessName",
                preview.getBusinessName()
        );

        modelAndView.addObject(
                "logoUrl",
                preview.getLogoUrl()
        );

        modelAndView.addObject(
                "description",
                preview.getDescription()
        );

        String canonicalUrl =
                preview.getPublicSlug() != null
                        && !preview.getPublicSlug().isBlank()
                        ? "https://" + preview.getPublicSlug() + "." + publicDomain
                        : "https://" + publicDomain + "/q/" + qrCode;

        modelAndView.addObject(
                "canonicalUrl",
                canonicalUrl
        );

        return modelAndView;
    }
}