package com.scanaura.ai.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiCategory {

    private String categoryName;

    private List<AiMenuItem> items;

}
