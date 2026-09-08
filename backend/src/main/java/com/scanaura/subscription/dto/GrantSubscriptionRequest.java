package com.scanaura.subscription.dto;

import com.scanaura.common.enums.BillingCycle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GrantSubscriptionRequest {

    @NotBlank(message = "Plan name is required.")
    private String planName;

    @NotNull(message = "Billing cycle is required.")
    private BillingCycle billingCycle;
}