package com.scanaura.activity.loyalty.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyClaimResponse {

    private UUID claimId;

    private UUID qrToken;

    private String qrType;

    private String qrVersion;

    private UUID rewardId;

    private String rewardTitle;

    private Integer pointsUsed;

    private BigDecimal rewardAmount;

    private String status;

    private Integer remainingPoints;

    private String message;

    public static LoyaltyClaimResponse create(
            UUID claimId,
            UUID qrToken,
            UUID rewardId,
            String rewardTitle,
            Integer pointsUsed,
            BigDecimal rewardAmount,
            String status,
            Integer remainingPoints
    ) {

        return LoyaltyClaimResponse.builder()
                .claimId(claimId)
                .qrToken(qrToken)
                .qrType("SCANAURA_LOYALTY_CLAIM")
                .qrVersion("1")
                .rewardId(rewardId)
                .rewardTitle(rewardTitle)
                .pointsUsed(pointsUsed)
                .rewardAmount(rewardAmount)
                .status(status)
                .remainingPoints(remainingPoints)
                .message(
                        "Reward claimed successfully. "
                                + "Show this QR code to the business."
                )
                .build();
    }
}