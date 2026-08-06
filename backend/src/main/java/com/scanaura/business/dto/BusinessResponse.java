package com.scanaura.business.dto;

import com.scanaura.common.enums.BusinessType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class BusinessResponse {

    private UUID id;

    private String businessName;

    private BusinessType businessType;

    private String logoUrl;

    private String phone;

    private String whatsapp;

    private String email;

    private String address;

    private String city;

    private String state;

    private String country;

    private String pincode;

    private String website;

    private String description;

    private String upiId;

    private String qrSlug;

    private Boolean active;

}