package com.scanaura.admin.controller;

import com.scanaura.admin.dto.QrInventoryResponse;
import com.scanaura.admin.service.AdminService;
import com.scanaura.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ApiResponse<String>> generateQr(
            @PathVariable int count
    ) {

        adminService.generatePhysicalQr(count);

        return ResponseEntity.ok(

                new ApiResponse<>(

                        true,

                        "QR generated successfully.",

                        "SUCCESS"

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

}
