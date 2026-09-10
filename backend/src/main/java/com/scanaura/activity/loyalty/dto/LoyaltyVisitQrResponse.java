package com.scanaura.activity.loyalty.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class LoyaltyVisitQrResponse {

    private UUID qrToken;
    private String qrType;
    private String qrVersion;
    private LocalDateTime expiresAt;

    public static LoyaltyVisitQrResponse create(
            UUID qrToken,
            LocalDateTime expiresAt
    ) {
        return new LoyaltyVisitQrResponse(
                qrToken,
                "SCANAURA_LOYALTY_VISIT",
                "1",
                expiresAt
        );
    }
}