package com.scanaura.ai.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiImportRequest {

    private Boolean overwriteExistingMenu;

    private List<AiCategory> categories;

}