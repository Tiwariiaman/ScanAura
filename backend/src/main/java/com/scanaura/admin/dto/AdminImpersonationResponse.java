package com.scanaura.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminImpersonationResponse {
    private String accessToken;
    private long expiresInSeconds;
}