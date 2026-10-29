package com.scanaura.referral.dto;

import com.scanaura.referral.entity.ReferralPartner;
import java.util.UUID;

public record ReferralPartnerResponse(UUID id, String name, String phone, String pin, boolean active, long businessCount) {
    public static ReferralPartnerResponse from(ReferralPartner partner, long businessCount) {
        return new ReferralPartnerResponse(partner.getId(), partner.getName(), partner.getPhone(), partner.getPin(), partner.isActive(), businessCount);
    }
}
