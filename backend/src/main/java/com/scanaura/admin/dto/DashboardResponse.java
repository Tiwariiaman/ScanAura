package com.scanaura.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private Long totalBusinesses;

    private Long activeBusinesses;

    private Long inactiveBusinesses;

    private Long trialSubscriptions;

    private Long activeSubscriptions;

    private Long expiredSubscriptions;

    private Long pendingSubscriptionRequests;

    private Long availablePhysicalQr;

    private Long assignedPhysicalQr;

    private Long digitalQrGenerated;

}
