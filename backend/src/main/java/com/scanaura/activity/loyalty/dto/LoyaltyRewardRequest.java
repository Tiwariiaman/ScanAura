package com.scanaura.activity.loyalty.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class LoyaltyRewardRequest {

    @NotBlank(message = "Reward title is required")
    @Size(max = 150, message = "Reward title cannot exceed 150 characters")
    private String title;

    @NotNull(message = "Points required is required")
    @Min(value = 1, message = "Points required must be greater than 0")
    private Integer pointsRequired;

    @NotNull(message = "Reward amount is required")
    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "Reward amount cannot be negative"
    )
    private BigDecimal rewardAmount;
}