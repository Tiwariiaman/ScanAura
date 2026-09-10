package com.scanaura.activity.loyalty.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyQrVerifyRequest {

    @NotNull(message = "QR token is required.")
    private UUID qrToken;

    private String type;

    private String version;
}