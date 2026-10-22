package com.scanaura.activity.scratch.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ScratchSettingsRequest {
    @NotBlank(message = "Scratch card name is required.")
    @Size(max = 150, message = "Scratch card name cannot exceed 150 characters.")
    private String name;

    @NotNull(message = "Enabled status is required.")
    private Boolean enabled;

    @NotNull(message = "Rewards list is required. Use an empty list when there are no rewards.")
    @Size(max = 6, message = "You can add a maximum of 6 rewards.")
    private List<@Valid ScratchRewardInput> rewards;
}
