package com.scanaura.activity.loyalty.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyCustomerRequest {

    @NotBlank(message = "Customer name is required.")
    @Size(max = 120, message = "Customer name cannot exceed 120 characters.")
    private String customerName;

    @NotBlank(message = "Mobile number is required.")
    @Size(max = 20, message = "Mobile number cannot exceed 20 characters.")
    @Pattern(
            regexp = "^[0-9+\\-() ]+$",
            message = "Invalid mobile number."
    )
    private String mobileNumber;
}