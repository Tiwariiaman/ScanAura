package com.scanaura.activity.scratch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScratchPublicStatusResponse {
    private String name;
    private boolean enabled;
}
