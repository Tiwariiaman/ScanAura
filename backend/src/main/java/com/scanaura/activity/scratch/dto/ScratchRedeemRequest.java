package com.scanaura.activity.scratch.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ScratchRedeemRequest {
    @NotNull(message = "Play ID is required.")
    private UUID playId;
}
