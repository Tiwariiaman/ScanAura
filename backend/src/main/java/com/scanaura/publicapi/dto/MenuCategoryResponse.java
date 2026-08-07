package com.scanaura.publicapi.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuCategoryResponse {

    private String categoryName;

    private List<MenuItemResponse> items;

}