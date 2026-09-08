package com.scanaura.subscription.controller;

import com.scanaura.common.response.ApiResponse;
import com.scanaura.subscription.dto.PendingSubscriptionRequestResponse;
import com.scanaura.subscription.dto.RejectRequest;
import com.scanaura.subscription.entity.Plan;
import com.scanaura.subscription.repository.PlanRepository;
import com.scanaura.subscription.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AdminSubscriptionController {

    private final SubscriptionService subscriptionService;

    private final PlanRepository planRepository;

    // ============================================================
    // PENDING SUBSCRIPTION REQUESTS
    // ============================================================

    @GetMapping("/api/v1/admin/subscription-requests/pending")
    public ResponseEntity<ApiResponse<List<PendingSubscriptionRequestResponse>>> getPendingRequests() {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Pending subscription requests fetched successfully.",
                        subscriptionService.getPendingRequests()
                )
        );
    }

    // ============================================================
    // APPROVE SUBSCRIPTION REQUEST
    // ============================================================

    @PostMapping("/api/v1/admin/subscription-requests/{requestId}/approve")
    public ResponseEntity<ApiResponse<String>> approveRequest(
            @PathVariable UUID requestId
    ) {

        subscriptionService.approveRequest(requestId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Subscription approved successfully.",
                        "SUCCESS"
                )
        );
    }

    // ============================================================
    // REJECT SUBSCRIPTION REQUEST
    // ============================================================

    @PostMapping("/api/v1/admin/subscription-requests/{requestId}/reject")
    public ResponseEntity<ApiResponse<String>> rejectRequest(
            @PathVariable UUID requestId,
            @Valid @RequestBody RejectRequest request
    ) {

        subscriptionService.rejectRequest(
                requestId,
                request
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Subscription rejected successfully.",
                        "SUCCESS"
                )
        );
    }

    // ============================================================
    // ACTIVE PLANS
    // ============================================================

    @GetMapping("/api/v1/admin/subscriptions/plans")
    public ResponseEntity<ApiResponse<List<Plan>>> getActivePlans() {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Active plans fetched successfully.",
                        planRepository
                                .findByActiveTrueOrderByNameAsc()
                )
        );
    }
}