package com.scanaura.activity.loyalty.controller;

import com.scanaura.activity.loyalty.dto.LoyaltyApiResponse;
import com.scanaura.activity.loyalty.dto.LoyaltyClaimRequest;
import com.scanaura.activity.loyalty.dto.LoyaltyClaimResponse;
import com.scanaura.activity.loyalty.dto.LoyaltyCustomerRequest;
import com.scanaura.activity.loyalty.dto.LoyaltyCustomerResponse;
import com.scanaura.activity.loyalty.dto.LoyaltyRewardResponse;
import com.scanaura.activity.loyalty.dto.LoyaltyVisitQrResponse;
import com.scanaura.activity.loyalty.service.LoyaltyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/public/loyalty")
@RequiredArgsConstructor
public class PublicLoyaltyController {

    private final LoyaltyService loyaltyService;


    // ============================================================
    // CUSTOMER
    // ============================================================

    @GetMapping("/customer")
    public ResponseEntity<LoyaltyApiResponse<LoyaltyCustomerResponse>>
    getCustomer(
            @RequestParam UUID businessId,
            @RequestParam String mobileNumber
    ) {

        return ResponseEntity.ok(
                LoyaltyApiResponse.success(
                        loyaltyService.getCustomer(
                                businessId,
                                mobileNumber
                        )
                )
        );
    }


    // ============================================================
    // CUSTOMER REGISTRATION / UPDATE
    // ============================================================

    @PostMapping("/customer")
    public ResponseEntity<LoyaltyApiResponse<LoyaltyCustomerResponse>>
    createOrUpdateCustomer(
            @RequestParam UUID businessId,
            @Valid @RequestBody LoyaltyCustomerRequest request
    ) {

        return ResponseEntity.ok(
                LoyaltyApiResponse.success(
                        "Loyalty account ready.",
                        loyaltyService.createOrUpdateCustomer(
                                businessId,
                                request
                        )
                )
        );
    }


    // ============================================================
    // GENERATE TEMPORARY VISIT QR
    // ============================================================

    @PostMapping("/visit/qr")
    public ResponseEntity<LoyaltyApiResponse<LoyaltyVisitQrResponse>>
    createVisitQr(
            @RequestParam UUID businessId,
            @RequestParam String mobileNumber
    ) {

        return ResponseEntity.ok(
                LoyaltyApiResponse.success(
                        "Loyalty visit QR generated.",
                        loyaltyService.createVisitQr(
                                businessId,
                                mobileNumber
                        )
                )
        );
    }


    // ============================================================
    // ACTIVE REWARDS
    // ============================================================

    @GetMapping("/rewards")
    public ResponseEntity<
            LoyaltyApiResponse<List<LoyaltyRewardResponse>>>
    getActiveRewards(
            @RequestParam UUID businessId
    ) {

        return ResponseEntity.ok(
                LoyaltyApiResponse.success(
                        loyaltyService.getActiveRewards(
                                businessId
                        )
                )
        );
    }


    // ============================================================
    // CLAIM REWARD
    // ============================================================

    @PostMapping("/claim")
    public ResponseEntity<LoyaltyApiResponse<LoyaltyClaimResponse>>
    claimReward(
            @RequestParam UUID businessId,
            @RequestParam String mobileNumber,
            @Valid @RequestBody LoyaltyClaimRequest request
    ) {

        return ResponseEntity.ok(
                LoyaltyApiResponse.success(
                        "Reward claimed successfully.",
                        loyaltyService.claimReward(
                                businessId,
                                mobileNumber,
                                request
                        )
                )
        );
    }
}