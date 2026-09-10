package com.scanaura.activity.loyalty.controller;

import com.scanaura.activity.loyalty.dto.*;
import com.scanaura.activity.loyalty.service.LoyaltyService;
import com.scanaura.business.entity.Business;
import com.scanaura.business.repository.BusinessRepository;
import com.scanaura.auth.entity.User;
import com.scanaura.common.exception.BusinessException;
import com.scanaura.common.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/activity/loyalty")
@RequiredArgsConstructor
public class LoyaltyController {

    private final LoyaltyService loyaltyService;
    private final BusinessRepository businessRepository;


    // ============================================================
    // SETTINGS
    // ============================================================

    @GetMapping("/settings")
    public ResponseEntity<
            LoyaltyApiResponse<LoyaltySettingsResponse>>
    getSettings() {

        UUID businessId = getBusinessId();

        return ResponseEntity.ok(
                LoyaltyApiResponse.success(
                        loyaltyService.getSettings(businessId)
                )
        );
    }


    @PutMapping("/settings")
    public ResponseEntity<
            LoyaltyApiResponse<LoyaltySettingsResponse>>
    updateSettings(
            @Valid @RequestBody LoyaltySettingsRequest request
    ) {

        UUID businessId = getBusinessId();

        return ResponseEntity.ok(
                LoyaltyApiResponse.success(
                        "Loyalty settings updated successfully.",
                        loyaltyService.updateSettings(
                                businessId,
                                request
                        )
                )
        );
    }


    // ============================================================
    // CUSTOMER
    // ============================================================

    @GetMapping("/customer")
    public ResponseEntity<
            LoyaltyApiResponse<LoyaltyCustomerResponse>>
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
    // REWARDS
    // ============================================================

    @GetMapping("/rewards")
    public ResponseEntity<
            LoyaltyApiResponse<List<LoyaltyRewardResponse>>>
    getRewards() {

        UUID businessId = getBusinessId();

        return ResponseEntity.ok(
                LoyaltyApiResponse.success(
                        loyaltyService.getRewards(businessId)
                )
        );
    }


    @GetMapping("/rewards/active")
    public ResponseEntity<
            LoyaltyApiResponse<List<LoyaltyRewardResponse>>>
    getActiveRewards(
            @RequestParam UUID businessId
    ) {

        return ResponseEntity.ok(
                LoyaltyApiResponse.success(
                        loyaltyService.getActiveRewards(businessId)
                )
        );
    }


    @PostMapping("/rewards")
    public ResponseEntity<
            LoyaltyApiResponse<LoyaltyRewardResponse>>
    createReward(
            @Valid @RequestBody LoyaltyRewardRequest request
    ) {

        UUID businessId = getBusinessId();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        LoyaltyApiResponse.success(
                                "Reward created successfully.",
                                loyaltyService.createReward(
                                        businessId,
                                        request
                                )
                        )
                );
    }


    @PutMapping("/rewards/{rewardId}")
    public ResponseEntity<
            LoyaltyApiResponse<LoyaltyRewardResponse>>
    updateReward(
            @PathVariable UUID rewardId,
            @Valid @RequestBody LoyaltyRewardUpdateRequest request
    ) {

        UUID businessId = getBusinessId();

        return ResponseEntity.ok(
                LoyaltyApiResponse.success(
                        loyaltyService.updateReward(
                                businessId,
                                rewardId,
                                request
                        )
                )
        );
    }


    @DeleteMapping("/rewards/{rewardId}")
    public ResponseEntity<
            LoyaltyApiResponse<Void>>
    deleteReward(
            @PathVariable UUID rewardId
    ) {

        UUID businessId = getBusinessId();

        loyaltyService.deleteReward(
                businessId,
                rewardId
        );

        return ResponseEntity.ok(
                LoyaltyApiResponse.success(
                        "Reward deleted successfully.",
                        null
                )
        );
    }


    // ============================================================
    // CLAIM REWARD
    // ============================================================

    @PostMapping("/claim")
    public ResponseEntity<
            LoyaltyApiResponse<LoyaltyClaimResponse>>
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


    // ============================================================
    // CLAIM QR PAYLOAD
    // ============================================================

    @GetMapping("/claim/{claimId}/qr")
    public ResponseEntity<
            LoyaltyApiResponse<String>>
    getClaimQrPayload(
            @PathVariable UUID claimId
    ) {

        UUID businessId = getBusinessId();

        return ResponseEntity.ok(
                LoyaltyApiResponse.success(
                        loyaltyService.createClaimQrPayload(
                                businessId,
                                claimId
                        )
                )
        );
    }


    // ============================================================
    // VERIFY / GRANT REWARD
    // ============================================================

    @PostMapping("/claim/verify")
    public ResponseEntity<
            LoyaltyApiResponse<LoyaltyClaimResponse>>
    verifyAndGrantReward(
            @Valid @RequestBody LoyaltyClaimVerifyRequest request
    ) {

        UUID businessId = getBusinessId();

        return ResponseEntity.ok(
                LoyaltyApiResponse.success(
                        "Reward verified and granted successfully.",
                        loyaltyService.verifyAndGrantReward(
                                businessId,
                                request
                        )
                )
        );
    }


    // ============================================================
    // VERIFY CUSTOMER VISIT QR
    // ============================================================

    @PostMapping("/visit/verify")
    public ResponseEntity<
            LoyaltyApiResponse<LoyaltyCustomerResponse>>
    verifyCustomerVisitQr(
            @Valid @RequestBody LoyaltyClaimVerifyRequest request
    ) {

        UUID businessId = getBusinessId();

        return ResponseEntity.ok(
                LoyaltyApiResponse.success(
                        "Visit verified and loyalty points added successfully.",
                        loyaltyService.verifyCustomerVisitQr(
                                businessId,
                                request.getQrToken()
                        )
                )
        );
    }


    // ============================================================
    // TRANSACTION HISTORY
    // ============================================================

    @GetMapping("/transactions")
    public ResponseEntity<
            LoyaltyApiResponse<List<LoyaltyTransactionResponse>>>
    getTransactions(
            @RequestParam UUID customerId
    ) {

        UUID businessId = getBusinessId();

        return ResponseEntity.ok(
                LoyaltyApiResponse.success(
                        loyaltyService.getTransactions(
                                businessId,
                                customerId
                        )
                )
        );
    }


    // ============================================================
    // BUSINESS ID RESOLUTION
    // ============================================================

    /**
     * Resolves the business belonging to the currently
     * authenticated user using ScanAura's existing
     * authentication/business relationship.
     *
     * SecurityUtil
     *      ↓
     * current User
     *      ↓
     * BusinessRepository.findByOwner(...)
     *      ↓
     * Business
     *      ↓
     * business.getId()
     */
    private UUID getBusinessId() {

        User currentUser =
                SecurityUtil.getCurrentUser();

        Business business =
                businessRepository
                        .findByOwner(currentUser)
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Business not found."
                                )
                        );

        return business.getId();
    }
}