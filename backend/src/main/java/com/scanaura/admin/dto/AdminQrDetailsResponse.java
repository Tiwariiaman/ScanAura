package com.scanaura.admin.dto;

import com.scanaura.common.enums.QrType;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminQrDetailsResponse {

    private UUID id;

    private String qrCode;

    private QrType type;

    private Boolean active;

    private Boolean assigned;

    private UUID businessId;

    private String businessName;
}