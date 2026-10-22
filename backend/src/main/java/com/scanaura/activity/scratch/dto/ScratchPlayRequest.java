package com.scanaura.activity.scratch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScratchPlayRequest {
    @NotBlank(message = "Customer name is required.")
    @Size(max = 120, message = "Customer name cannot exceed 120 characters.")
    private String customerName;

    @NotBlank(message = "Mobile number is required.")
    @Pattern(regexp = "^[0-9+() -]{7,20}$", message = "Enter a valid mobile number.")
    private String mobileNumber;
}
