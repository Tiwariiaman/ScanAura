package com.scanaura.activity.loyalty.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoyaltySettingsRequest {

    @NotNull(message = "Loyalty enabled status is required")
    private Boolean enabled;
}