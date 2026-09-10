package com.scanaura.activity.loyalty.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyQrResponse {

    private UUID qrToken;

    private String type;

    private String version;

    public static LoyaltyQrResponse fromToken(
            UUID qrToken
    ) {
        return LoyaltyQrResponse.builder()
                .qrToken(qrToken)
                .type("SCANAURA_LOYALTY_CLAIM")
                .version("1")
                .build();
    }
}