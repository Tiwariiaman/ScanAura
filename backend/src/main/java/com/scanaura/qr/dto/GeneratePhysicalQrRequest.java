package com.scanaura.qr.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeneratePhysicalQrRequest {

    @Min(value = 1, message = "Minimum 1 QR Code")
    @Max(value = 1000, message = "Maximum 1000 QR Codes")
    private int count;

}