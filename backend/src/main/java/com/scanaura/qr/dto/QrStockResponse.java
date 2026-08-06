package com.scanaura.qr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrStockResponse {

    private long availablePhysicalQr;

    private long assignedPhysicalQr;

    private long digitalQr;

    private long totalQr;

}