package com.scanaura.activity.scratch.dto;

import com.scanaura.activity.scratch.entity.ScratchPlay;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ScratchPlayResponse {
    private UUID playId;
    private String rewardName;
    private String couponCode;
    private UUID couponToken;
    private ScratchPlay.PlayStatus status;
    private LocalDateTime playedAt;
}
