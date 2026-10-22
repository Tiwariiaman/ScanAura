
package com.scanaura.activity.loyalty.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class LoyaltyVisitVerifyRequest {

    @NotNull(message = "QR token is required.")
    private UUID qrToken;

    @NotNull(message = "Bill amount is required.")
    @DecimalMin(
            value = "1.00",
            message = "Bill amount must be at least ₹1."
    )
    @DecimalMax(
            value = "1000000.00",
            message = "Bill amount cannot exceed ₹10,00,000."
    )
    @Digits(
            integer = 8,
            fraction = 2,
            message = "Bill amount can have up to 2 decimal places."
    )
    private BigDecimal billAmount;
}
