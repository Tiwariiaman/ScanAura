package com.scanaura.activity.loyalty.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class LoyaltyClaimRequest {

    @NotNull(message = "Reward ID is required")
    private UUID rewardId;
}