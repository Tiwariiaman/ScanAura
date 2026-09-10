package com.scanaura.activity.loyalty.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyRewardResponse {

    private UUID id;

    private UUID businessId;

    private String title;

    private String description;

    private Integer pointsRequired;

    private BigDecimal rewardAmount;

    private Boolean active;
}