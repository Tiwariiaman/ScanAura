package com.scanaura.subscription.dto;

import com.scanaura.common.enums.BillingCycle;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingSubscriptionResponse {

    private UUID subscriptionId;

    private UUID businessId;

    private String businessName;

    private String planName;

    private BillingCycle billingCycle;

    private String transactionId;

    private String paymentScreenshotUrl;

    private LocalDate requestedDate;

}