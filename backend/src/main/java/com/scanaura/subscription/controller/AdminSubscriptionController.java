package com.scanaura.subscription.controller;

import com.scanaura.common.response.ApiResponse;
import com.scanaura.subscription.dto.PendingSubscriptionRequestResponse;
import com.scanaura.subscription.dto.RejectRequest;
import com.scanaura.subscription.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/subscription-requests")
@RequiredArgsConstructor
public class AdminSubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<PendingSubscriptionRequestResponse>>> getPendingRequests() {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Pending subscription requests fetched successfully.",
                        subscriptionService.getPendingRequests()
                )
        );
    }

    @PostMapping("/{requestId}/approve")
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

    @PostMapping("/{requestId}/reject")
    public ResponseEntity<ApiResponse<String>> rejectRequest(
            @PathVariable UUID requestId,
            @Valid @RequestBody RejectRequest request
    ) {

        subscriptionService.rejectRequest(requestId, request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Subscription rejected successfully.",
                        "SUCCESS"
                )
        );
    }

}
