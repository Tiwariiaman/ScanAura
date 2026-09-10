package com.scanaura.business.entity;

import com.scanaura.auth.entity.User;
import com.scanaura.common.entity.BaseEntity;
import com.scanaura.common.enums.BusinessType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "businesses")
@Getter
@Setter
public class Business extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private String businessName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BusinessType businessType;

    private String logoUrl;

    @Column(length = 500)
    private String logoPublicId;

    @Column(nullable = false)
    private String phone;

    private String whatsapp;

    private String email;

    private String address;

    private String city;

    private String state;

    private String country;

    private String pincode;

    private String website;

    @Column(length = 1000)
    private String description;

    private String upiId;

    @Column(length = 500)
    private String googleReviewUrl;

    @Column(nullable = false)
    private Boolean googleReviewEnabled = false;

    @Column(nullable = false)
    private Boolean paymentEnabled = true;

    @Column(nullable = false, unique = true)
    private String qrSlug;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(length = 500)
    private String instagramUrl;

    @Column(nullable = false)
    private Boolean instagramEnabled = false;

    @Column(length = 500)
    private String facebookUrl;

    @Column(nullable = false)
    private Boolean facebookEnabled = false;

    @Column(length = 500)
    private String youtubeUrl;

    @Column(nullable = false)
    private Boolean youtubeEnabled = false;

    @Column(name = "brand_color", length = 7)
    private String brandColor;
}