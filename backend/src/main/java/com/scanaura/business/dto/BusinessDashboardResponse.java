package com.scanaura.business.dto;

import com.scanaura.common.enums.SubscriptionStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessDashboardResponse {

    private UUID businessId;

    private String businessName;

    private String ownerName;

    private String email;

    private String phone;

    private String city;

    private Boolean active;

    private SubscriptionStatus subscriptionStatus;

    private String currentPlan;

    private Long todayScans;

    private Long yesterdayScans;

    private Long last7DaysScans;

    private Long totalScans;
}