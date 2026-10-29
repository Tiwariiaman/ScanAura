package com.scanaura.publicapi.controller;

import com.scanaura.common.response.ApiResponse;
import com.scanaura.publicapi.dto.LandingResponse;
import com.scanaura.publicapi.dto.MenuResponse;
import com.scanaura.publicapi.dto.PaymentResponse;
import com.scanaura.publicapi.dto.SharePreviewResponse;
import com.scanaura.publicapi.service.PublicService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

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
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Landing page fetched successfully.",
                publicService.getLandingPage(qrCode)
        ));
    }

    @GetMapping("/q/{qrCode}/menu")
    public ResponseEntity<ApiResponse<MenuResponse>> menu(
            @PathVariable String qrCode) {
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Menu fetched successfully.",
                publicService.getMenu(qrCode)
        ));
    }

    @GetMapping("/q/{qrCode}/payment")
    public ResponseEntity<ApiResponse<PaymentResponse>> payment(
            @PathVariable String qrCode) {
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Payment details fetched successfully.",
                publicService.getPaymentDetails(qrCode)
        ));
    }

    /**
     * Server-rendered social metadata endpoint. Share links should target this API URL
     * so crawlers receive item name/image before the browser is redirected to Flutter.
     */
    @GetMapping("/share/{qrCode}")
    public ModelAndView sharePreview(
            @PathVariable String qrCode,
            @RequestParam(required = false) String itemId) {
        SharePreviewResponse preview = publicService.getSharePreview(qrCode, itemId);
        ModelAndView view = new ModelAndView("public/share-preview");

        view.addObject("businessName", preview.getBusinessName());
        view.addObject("logoUrl", preview.getLogoUrl());
        view.addObject("description", preview.getDescription());

        String encodedQrCode = UriUtils.encodePathSegment(qrCode, StandardCharsets.UTF_8);
        String redirectUrl = "https://" + publicDomain + "/q/" + encodedQrCode;
        if (preview.getItemName() != null && !preview.getItemName().isBlank()) {
            String encodedItemId = UriUtils.encodeQueryParam(itemId.trim(), StandardCharsets.UTF_8);
            redirectUrl += "/menu?itemId=" + encodedItemId;
        }

        String previewTitle = preview.getItemName() != null && !preview.getItemName().isBlank()
                ? preview.getItemName() + " | " + preview.getBusinessName()
                : preview.getBusinessName();
        String previewDescription = preview.getItemDescription();
        if (previewDescription == null || previewDescription.isBlank()) {
            previewDescription = preview.getItemName() != null && !preview.getItemName().isBlank()
                    ? "Check out " + preview.getItemName() + " at " + preview.getBusinessName() + " on ScanAura."
                    : preview.getDescription();
        }
        String previewImageUrl = preview.getItemImageUrl() != null && !preview.getItemImageUrl().isBlank()
                ? preview.getItemImageUrl()
                : preview.getLogoUrl();

        // Keep the canonical URL aligned with the API preview URL being shared; the final
        // destination remains redirectUrl and is used by the HTML meta refresh.
        String canonicalUrl = "https://api.scanaura.in/api/public/share/" + encodedQrCode;
        if (preview.getItemName() != null && !preview.getItemName().isBlank()) {
            canonicalUrl += "?itemId="
                    + UriUtils.encodeQueryParam(itemId.trim(), StandardCharsets.UTF_8);
        }

        view.addObject("canonicalUrl", canonicalUrl);
        view.addObject("redirectUrl", redirectUrl);
        view.addObject("previewTitle", previewTitle);
        view.addObject("previewDescription", previewDescription);
        view.addObject("previewImageUrl", previewImageUrl);
        view.addObject("itemName", preview.getItemName());
        return view;
    }
}
