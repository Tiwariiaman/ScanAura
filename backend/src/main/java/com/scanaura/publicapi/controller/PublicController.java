package com.scanaura.publicapi.controller;

import com.scanaura.common.response.ApiResponse;
import com.scanaura.publicapi.dto.LandingResponse;
import com.scanaura.publicapi.dto.MenuResponse;
import com.scanaura.publicapi.dto.PaymentResponse;
import com.scanaura.publicapi.service.PublicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final PublicService publicService;

    @GetMapping("/q/{qrCode}")
    public ResponseEntity<ApiResponse<LandingResponse>> landing(
            @PathVariable String qrCode
    ) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Landing page loaded.",
                        publicService.getLandingPage(qrCode)
                )
        );
    }

    @GetMapping("/q/{qrCode}/menu")
    public ResponseEntity<ApiResponse<MenuResponse>> menu(
            @PathVariable String qrCode
    ) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Menu loaded.",
                        publicService.getMenu(qrCode)
                )
        );
    }

    @GetMapping("/q/{qrCode}/payment")
    public ResponseEntity<ApiResponse<PaymentResponse>> payment(
            @PathVariable String qrCode
    ) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Payment details loaded.",
                        publicService.getPaymentDetails(qrCode)
                )
        );
    }
}
