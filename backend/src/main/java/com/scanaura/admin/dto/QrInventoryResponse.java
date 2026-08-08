package com.scanaura.admin.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrInventoryResponse {

    private Long availablePhysicalQr;

    private Long assignedPhysicalQr;

    private Long digitalQr;

    private Long totalQr;

}
