package com.scanaura.catalog.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class CatalogRequest {

    private UUID categoryId;

    @NotBlank(message = "Item name is required")
    private String name;

    private String description;

    // Optional: null means the business has not provided a price yet.
    @DecimalMin(
            value = "0.0",
            inclusive = false,
            message = "Price must be greater than zero when provided"
    )
    private BigDecimal price;

    private String imageUrl;

    private String imagePublicId;

    private Boolean veg;

    private Boolean available = true;

    private Boolean bestSeller = false;

    private Boolean recommended = false;

    private Integer displayOrder = 0;
}