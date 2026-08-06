package com.scanaura.catalog.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class CatalogRequest {

    // Optional
    private UUID categoryId;

    @NotBlank(message = "Item name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    private BigDecimal price;

    private String imageUrl;

    private Boolean veg = true;

    private Boolean available = true;

    private Boolean bestSeller = false;

    private Boolean recommended = false;

    private Integer displayOrder = 0;

}