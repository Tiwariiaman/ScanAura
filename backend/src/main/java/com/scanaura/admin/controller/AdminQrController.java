package com.scanaura.admin.controller;

import com.scanaura.admin.dto.AdminQrDetailsResponse;
import com.scanaura.admin.dto.QrInventoryResponse;
import com.scanaura.admin.service.AdminService;
import com.scanaura.common.response.ApiResponse;
import com.scanaura.qr.dto.QrResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/qr")
@RequiredArgsConstructor
public class AdminQrController {

    private final AdminService adminService;

    @GetMapping("/inventory")
    public ResponseEntity<ApiResponse<QrInventoryResponse>> inventory() {

        return ResponseEntity.ok(

                new ApiResponse<>(

                        true,

                        "QR inventory fetched successfully.",

                        adminService.getQrInventory()

                )

        );

    }

    @PostMapping("/generate/{count}")
    public ResponseEntity<ApiResponse<List<QrResponse>>> generateQr(
            @PathVariable int count
    ) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "QR codes generated successfully.",
                                adminService.generatePhysicalQr(count)
                        )
                );
    }

    @PatchMapping("/deactivate/{qrCode}")
    public ResponseEntity<ApiResponse<String>> deactivateQr(
            @PathVariable String qrCode
    ) {

        adminService.deactivateQr(qrCode);

        return ResponseEntity.ok(

                new ApiResponse<>(

                        true,

                        "QR deactivated successfully.",

                        "SUCCESS"

                )

        );

    }

    @GetMapping("/{qrCode}")
    public ResponseEntity<ApiResponse<AdminQrDetailsResponse>> getQrDetails(
            @PathVariable String qrCode
    ) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "QR details fetched successfully.",
                        adminService.getQrDetails(qrCode)
                )
        );
    }



}
