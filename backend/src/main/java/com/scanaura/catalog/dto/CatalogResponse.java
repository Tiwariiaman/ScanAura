package com.scanaura.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogResponse {

    private UUID id;

    private UUID categoryId;

    private String categoryName;

    private String name;

    private String description;

    private BigDecimal price;

    private String imageUrl;

    private Boolean veg;

    private Boolean available;

    private Boolean bestSeller;

    private Boolean recommended;

    private Integer displayOrder;

    private Boolean active;

}