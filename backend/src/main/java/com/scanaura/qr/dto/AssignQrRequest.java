package com.scanaura.qr.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AssignQrRequest {

    @NotNull(message = "Business ID is required")
    private UUID businessId;

    @NotEmpty(message = "At least one QR Code is required")
    private List<String> qrCodes;

}