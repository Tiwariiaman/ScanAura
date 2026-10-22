package com.scanaura.activity.scratch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScratchRewardInput {
    @NotBlank(message = "Reward name is required.")
    @Size(max = 150, message = "Reward name cannot exceed 150 characters.")
    private String name;
}
