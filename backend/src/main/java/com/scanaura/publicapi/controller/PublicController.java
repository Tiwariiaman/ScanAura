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
import org.springframework.web.bind.annotation.RequestParam;
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
            @PathVariable String qrCode,
            @RequestParam(required = false) String itemId) {

        SharePreviewResponse preview =
                publicService.getSharePreview(qrCode, itemId);

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

        String redirectUrl = "https://" + publicDomain + "/q/" + qrCode;
        if (preview.getItemName() != null && !preview.getItemName().isBlank()) {
            redirectUrl += "/menu?itemId=" +
                    org.springframework.web.util.UriUtils.encodeQueryParam(
                            itemId.trim(), java.nio.charset.StandardCharsets.UTF_8);
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

        modelAndView.addObject("canonicalUrl", redirectUrl);
        modelAndView.addObject("redirectUrl", redirectUrl);
        modelAndView.addObject("previewTitle", previewTitle);
        modelAndView.addObject("previewDescription", previewDescription);
        modelAndView.addObject("previewImageUrl", previewImageUrl);
        modelAndView.addObject("itemName", preview.getItemName());

        return modelAndView;
    }
}
