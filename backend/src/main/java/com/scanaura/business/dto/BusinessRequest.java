package com.scanaura.business.dto;

import com.scanaura.common.enums.BusinessType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessRequest {

    @NotBlank(message = "Business name is required")
    private String businessName;

    @NotNull(message = "Business type is required")
    private BusinessType businessType;

    @NotBlank(message = "Phone number is required")
    private String phone;

    private String logoUrl;

    private String logoPublicId;

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

    private String googleReviewUrl;

    private Boolean googleReviewEnabled;

    private Boolean paymentEnabled;

    private String instagramUrl;
    private Boolean instagramEnabled;

    private String facebookUrl;
    private Boolean facebookEnabled;

    private String youtubeUrl;
    private Boolean youtubeEnabled;

    @Pattern(
            regexp = "^$|^#[0-9A-Fa-f]{6}$",
            message = "Brand color must be a valid HEX color such as #E63946"
    )
    private String brandColor;
}