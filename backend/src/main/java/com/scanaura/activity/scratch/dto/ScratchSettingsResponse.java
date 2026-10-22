package com.scanaura.activity.scratch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ScratchSettingsResponse {
    private UUID id;
    private UUID businessId;
    private String name;
    private boolean enabled;
    private List<String> rewards;
}
