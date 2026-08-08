package com.scanaura.subscription.controller;

import com.scanaura.common.response.ApiResponse;
import com.scanaura.subscription.dto.SubscriptionRequestDto;
import com.scanaura.subscription.dto.SubscriptionRequestHistoryResponse;
import com.scanaura.subscription.dto.SubscriptionResponse;
import com.scanaura.subscription.dto.UpgradeSubscriptionRequest;
import com.scanaura.subscription.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> getMySubscription() {

        SubscriptionResponse response =
                subscriptionService.getMySubscription();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Subscription fetched successfully.",
                        response
                )
        );
    }

//    @PostMapping("/upgrade")
//    public ResponseEntity<ApiResponse<String>> requestUpgrade(
//            @Valid @RequestBody UpgradeSubscriptionRequest request
//    ) {
//
//        subscriptionService.requestUpgrade(request);
//
//        return ResponseEntity.ok(
//                new ApiResponse<>(
//                        true,
//                        "Upgrade request submitted successfully.",
//                        "Waiting for admin approval."
//                )
//        );
//    }

    @PostMapping("/request")
    public ResponseEntity<ApiResponse<String>>
    createSubscriptionRequest(

            @Valid
            @RequestBody
            SubscriptionRequestDto request

    ) {

        subscriptionService.createSubscriptionRequest(request);

        return ResponseEntity.ok(

                new ApiResponse<>(

                        true,

                        "Subscription request submitted successfully.",

                        "Waiting for admin approval."

                )

        );

    }

    @GetMapping("/request/history")
    public ResponseEntity<ApiResponse<List<SubscriptionRequestHistoryResponse>>> getRequestHistory() {

        return ResponseEntity.ok(

                new ApiResponse<>(

                        true,

                        "Subscription history fetched successfully.",

                        subscriptionService.getRequestHistory()

                )

        );

    }
}
