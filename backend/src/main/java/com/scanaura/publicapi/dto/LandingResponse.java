package com.scanaura.publicapi.dto;

import com.scanaura.common.enums.BusinessType;
import lombok.*;

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
}