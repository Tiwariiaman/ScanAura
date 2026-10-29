package com.scanaura.auth.entity;

import com.scanaura.common.entity.BaseEntity;
import com.scanaura.common.enums.UserRole;
import com.scanaura.referral.entity.ReferralPartner;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String mobile;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private Boolean verified = false;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private Boolean deleted = false;

    @Column(name = "registration_pin", length = 6)
    private String registrationPin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referral_partner_id")
    private ReferralPartner referralPartner;

    @Column(name = "super_admin_pin_used", nullable = false)
    private boolean superAdminPinUsed = false;

}
