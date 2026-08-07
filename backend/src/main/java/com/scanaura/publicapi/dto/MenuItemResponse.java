package com.scanaura.publicapi.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemResponse {

    private String name;

    private String description;

    private BigDecimal price;

    private String imageUrl;

    private Boolean veg;

    private Boolean available;

    private Boolean bestSeller;

    private Boolean recommended;

}