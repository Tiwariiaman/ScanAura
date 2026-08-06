package com.scanaura.qr.controller;

import com.scanaura.common.response.ApiResponse;
import com.scanaura.qr.dto.AssignQrRequest;
import com.scanaura.qr.dto.GeneratePhysicalQrRequest;
import com.scanaura.qr.dto.QrResponse;
import com.scanaura.qr.dto.QrStockResponse;
import com.scanaura.qr.service.QrService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/qr")
@RequiredArgsConstructor
public class QrController {

    private final QrService qrService;

    // Generate Physical QR Codes
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<List<QrResponse>>> generateQrCodes(
            @Valid @RequestBody GeneratePhysicalQrRequest request
    ) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "QR Codes generated successfully.",
                                qrService.generatePhysicalQrCodes(
                                        request.getCount()
                                )
                        )
                );
    }

    // Assign Physical QR Codes
    @PostMapping("/assign")
    public ResponseEntity<ApiResponse<List<QrResponse>>> assignQrCodes(
            @Valid @RequestBody AssignQrRequest request
    ) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "QR Codes assigned successfully.",
                        qrService.assignPhysicalQrCodes(request)
                )
        );
    }

    // Logged-in Business QR Codes
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<QrResponse>>> getMyQrCodes() {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "QR Codes fetched successfully.",
                        qrService.getMyQrCodes()
                )
        );
    }

    // Available Physical QR Codes
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<QrResponse>>> getAvailableQrCodes() {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Available QR Codes fetched successfully.",
                        qrService.getAvailablePhysicalQrCodes()
                )
        );
    }

    // QR Inventory
    @GetMapping("/inventory")
    public ResponseEntity<ApiResponse<QrStockResponse>> getInventory() {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "QR Inventory fetched successfully.",
                        qrService.getQrInventory()
                )
        );
    }

    // Deactivate QR
    @PutMapping("/deactivate/{qrCode}")
    public ResponseEntity<ApiResponse<String>> deactivateQr(
            @PathVariable String qrCode
    ) {

        qrService.deactivateQr(qrCode);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "QR Code deactivated successfully.",
                        "Success"
                )
        );
    }

    @GetMapping("/digital")
    public ResponseEntity<ApiResponse<QrResponse>> getMyDigitalQr() {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Digital QR fetched successfully.",
                        qrService.getMyDigitalQr()
                )
        );
    }
}
