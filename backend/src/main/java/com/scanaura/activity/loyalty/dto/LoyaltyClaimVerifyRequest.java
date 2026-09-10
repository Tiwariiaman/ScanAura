package com.scanaura.activity.loyalty.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyClaimVerifyRequest {

    @NotNull(message = "QR token is required.")
    private UUID qrToken;
}