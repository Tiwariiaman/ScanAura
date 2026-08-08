package com.scanaura.subscription.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RejectRequest {

    @NotBlank(message = "Remark is required.")
    private String remark;

}
