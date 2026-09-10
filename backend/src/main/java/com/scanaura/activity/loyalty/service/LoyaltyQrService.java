package com.scanaura.activity.loyalty.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LoyaltyQrService {

    public String createQrPayload(UUID qrToken) {

        if (qrToken == null) {
            throw new IllegalArgumentException(
                    "QR token is required."
            );
        }

        return """
                {
                  "qrToken": "%s",
                  "type": "SCANAURA_LOYALTY_CLAIM",
                  "version": "1"
                }
                """.formatted(qrToken);
    }
}