package com.scanaura.referral.entity;

import com.scanaura.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "referral_partners", uniqueConstraints = {
    @UniqueConstraint(name = "uk_referral_partner_pin", columnNames = "pin")
})
@Getter
@Setter
public class ReferralPartner extends BaseEntity {
    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 10)
    private String phone;

    @Column(nullable = false, unique = true, length = 6)
    private String pin;

    @Column(nullable = false)
    private boolean active = true;
}
