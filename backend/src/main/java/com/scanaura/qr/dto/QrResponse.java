package com.scanaura.qr.dto;

import com.scanaura.common.enums.QrType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrResponse {

    private UUID id;

    private String qrCode;

    private QrType type;

    private Boolean assigned;

    private Boolean active;

    private UUID businessId;

    private String businessName;

}