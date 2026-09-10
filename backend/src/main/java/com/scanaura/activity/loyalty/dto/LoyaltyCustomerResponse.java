package com.scanaura.activity.loyalty.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyCustomerResponse {

    private UUID id;

    private UUID businessId;

    private String customerName;

    private String mobileNumber;

    private Integer pointsBalance;

    private Integer totalPointsEarned;

    private Integer totalPointsRedeemed;

    /**
     * Points awarded by the latest successful loyalty action.
     *
     * This is populated by visit verification.
     * It is null for normal customer/profile responses.
     */
    private Integer pointsAwarded;
}