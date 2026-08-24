package com.scanaura.publicapi.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicQrResponse {

    private String qrCode;

    private UUID businessId;

    private String businessName;

    private String qrType;

    private Boolean active;

    private Boolean assigned;
}