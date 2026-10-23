package com.scanaura.publicapi.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SharePreviewResponse {

    private String businessName;
    private String logoUrl;
    private String description;
    private String businessType;
    private String publicSlug;

    private String phone;
    private String whatsapp;
    private String address;
    private String city;
    private String state;
    private String country;
    private String pincode;
    private String website;
    private String googleMapsUrl;
    private String googleReviewUrl;
    private String instagramUrl;
    private String facebookUrl;
    private String youtubeUrl;

    private List<MenuCategoryResponse> menu;
}
