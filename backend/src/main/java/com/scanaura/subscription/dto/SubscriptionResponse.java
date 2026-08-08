package com.scanaura.subscription.dto;

import com.scanaura.common.enums.BillingCycle;
import com.scanaura.common.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {

    private String planName;

    private SubscriptionStatus status;

    private BillingCycle billingCycle;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer trialDaysLeft;

    private Integer aiImportLimit;

    private Integer aiImportUsed;

    private Boolean brandedQr;

    private Boolean prioritySupport;

}