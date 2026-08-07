package com.scanaura.publicapi.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuResponse {

    private String businessName;

    private String logoUrl;

    private List<MenuCategoryResponse> menu;

}