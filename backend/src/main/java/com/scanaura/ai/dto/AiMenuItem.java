package com.scanaura.ai.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiMenuItem {

    private String name;

    private String description;

    private BigDecimal price;

    private Boolean veg;

}
