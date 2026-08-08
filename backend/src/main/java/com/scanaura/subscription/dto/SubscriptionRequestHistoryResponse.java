package com.scanaura.subscription.dto;

import com.scanaura.common.enums.BillingCycle;
import com.scanaura.common.enums.RequestStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequestHistoryResponse {

    private String planName;

    private BillingCycle billingCycle;

    private RequestStatus status;

    private String transactionId;

    private String paymentScreenshotUrl;

    private String adminRemark;

    private LocalDateTime requestedAt;

}