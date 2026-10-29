package com.scanaura.referral.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReferralPartnerStatusRequest {
    @NotNull private Boolean active;
}
