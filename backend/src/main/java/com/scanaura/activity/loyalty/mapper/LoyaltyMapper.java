package com.scanaura.activity.loyalty.mapper;

import com.scanaura.activity.loyalty.dto.LoyaltyClaimResponse;
import com.scanaura.activity.loyalty.dto.LoyaltyCustomerResponse;
import com.scanaura.activity.loyalty.dto.LoyaltyRewardResponse;
import com.scanaura.activity.loyalty.dto.LoyaltySettingsResponse;
import com.scanaura.activity.loyalty.dto.LoyaltyTransactionResponse;
import com.scanaura.activity.loyalty.entity.LoyaltyClaim;
import com.scanaura.activity.loyalty.entity.LoyaltyCustomer;
import com.scanaura.activity.loyalty.entity.LoyaltyReward;
import com.scanaura.activity.loyalty.entity.LoyaltySettings;
import com.scanaura.activity.loyalty.entity.LoyaltyTransaction;
import org.springframework.stereotype.Component;

@Component
public class LoyaltyMapper {

    // ============================================================
    // SETTINGS
    // ============================================================

    public LoyaltySettingsResponse toSettingsResponse(
            LoyaltySettings settings
    ) {

        return LoyaltySettingsResponse.builder()
                .businessId(settings.getBusinessId())
                .enabled(settings.getEnabled())
                .pointsPerVisit(settings.getPointsPerVisit())
                .build();
    }


    // ============================================================
    // CUSTOMER
    // ============================================================

    public LoyaltyCustomerResponse toCustomerResponse(
            LoyaltyCustomer customer
    ) {

        return LoyaltyCustomerResponse.builder()
                .id(customer.getId())
                .businessId(customer.getBusinessId())
                .customerName(customer.getCustomerName())
                .mobileNumber(customer.getMobileNumber())
                .pointsBalance(customer.getPointsBalance())
                .totalPointsEarned(customer.getTotalPointsEarned())
                .totalPointsRedeemed(customer.getTotalPointsRedeemed())
                .build();
    }


    // ============================================================
    // REWARD
    // ============================================================

    public LoyaltyRewardResponse toRewardResponse(
            LoyaltyReward reward
    ) {

        return LoyaltyRewardResponse.builder()
                .id(reward.getId())
                .businessId(reward.getBusinessId())
                .title(reward.getTitle())
                .pointsRequired(reward.getPointsRequired())
                .rewardAmount(reward.getRewardAmount())
                .active(reward.getActive())
                .build();
    }


    // ============================================================
    // TRANSACTION
    // ============================================================

    public LoyaltyTransactionResponse toTransactionResponse(
            LoyaltyTransaction transaction
    ) {

        return LoyaltyTransactionResponse.builder()
                .id(transaction.getId())
                .businessId(transaction.getBusinessId())
                .customerId(transaction.getCustomerId())
                .transactionType(
                        transaction.getTransactionType()
                )
                .points(transaction.getPoints())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .build();
    }


    // ============================================================
    // CLAIM
    // ============================================================

    public LoyaltyClaimResponse toClaimResponse(
            LoyaltyClaim claim,
            LoyaltyReward reward,
            Integer remainingPoints
    ) {

        return LoyaltyClaimResponse.create(
                claim.getId(),
                claim.getQrToken(),
                reward.getId(),
                reward.getTitle(),
                claim.getPointsUsed(),
                claim.getRewardAmount(),
                claim.getStatus().name(),
                remainingPoints
        );
    }
}