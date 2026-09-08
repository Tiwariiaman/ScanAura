package com.scanaura.admin.controller;

import com.scanaura.admin.dto.BusinessSummaryResponse;
import com.scanaura.admin.service.AdminService;
import com.scanaura.common.enums.BillingCycle;
import com.scanaura.common.response.ApiResponse;
import com.scanaura.subscription.dto.GrantSubscriptionRequest;
import com.scanaura.subscription.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/businesses")
@RequiredArgsConstructor
public class AdminBusinessController {

    private final AdminService adminService;
    private final SubscriptionService subscriptionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BusinessSummaryResponse>>> getAllBusinesses() {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Businesses fetched successfully.",
                        adminService.getAllBusinesses()
                )
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<BusinessSummaryResponse>>> searchBusinesses(
            @RequestParam String keyword
    ) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Businesses fetched successfully.",
                        adminService.searchBusinesses(keyword)
                )
        );
    }

    @PatchMapping("/{businessId}/activate")
    public ResponseEntity<ApiResponse<String>> activateBusiness(
            @PathVariable UUID businessId
    ) {

        adminService.activateBusiness(businessId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Business activated successfully.",
                        "SUCCESS"
                )
        );
    }

    @PatchMapping("/{businessId}/deactivate")
    public ResponseEntity<ApiResponse<String>> deactivateBusiness(
            @PathVariable UUID businessId
    ) {

        adminService.deactivateBusiness(businessId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Business deactivated successfully.",
                        "SUCCESS"
                )
        );
    }

    @PatchMapping("/{businessId}/subscription")
    public ResponseEntity<ApiResponse<String>> grantSubscription(
            @PathVariable UUID businessId,
            @Valid @RequestBody GrantSubscriptionRequest request
    ) {

        subscriptionService.grantSubscription(
                businessId,
                request.getPlanName(),
                request.getBillingCycle()
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Subscription granted successfully.",
                        "SUCCESS"
                )
        );
    }
}