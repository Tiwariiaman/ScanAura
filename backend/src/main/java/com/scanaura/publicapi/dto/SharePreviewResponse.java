package com.scanaura.publicapi.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SharePreviewResponse {

    private String businessName;

    private String logoUrl;

    private String description;
}