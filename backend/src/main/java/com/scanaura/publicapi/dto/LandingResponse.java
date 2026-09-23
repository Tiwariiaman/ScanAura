package com.scanaura.publicapi.dto;

import com.scanaura.common.enums.BusinessType;
import com.scanaura.gallery.dto.GalleryImageResponse;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LandingResponse {

    private String businessName;
    private BusinessType businessType;
    private String city;
    private String logoUrl;

    private Boolean menuAvailable;

    private Boolean paymentEnabled;

    private String googleReviewUrl;
    private Boolean googleReviewEnabled;

    private String instagramUrl;
    private Boolean instagramEnabled;

    private String facebookUrl;
    private Boolean facebookEnabled;

    private String youtubeUrl;
    private Boolean youtubeEnabled;

    private Boolean loyaltyEnabled;

    private UUID businessId;

    private String brandColor;

    // Contact
    private String phone;
    private Boolean callEnabled;

    private String whatsapp;
    private Boolean whatsappEnabled;

    // Location
    private String address;
    private String state;
    private String country;
    private String pincode;

    private String googleMapsUrl;
    private Boolean mapsEnabled;

    // Gallery
    // Gallery
    private Boolean galleryEnabled;
    private List<GalleryImageResponse> galleryImages;
}