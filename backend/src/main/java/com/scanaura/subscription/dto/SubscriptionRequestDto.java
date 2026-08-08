package com.scanaura.subscription.dto;

import com.scanaura.common.enums.BillingCycle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequestDto {

    @NotBlank(message = "Plan name is required.")
    private String planName;

    @NotNull(message = "Billing cycle is required.")
    private BillingCycle billingCycle;

    @NotBlank(message = "Transaction ID is required.")
    private String transactionId;

    @NotBlank(message = "Payment screenshot is required.")
    private String paymentScreenshotUrl;

}
