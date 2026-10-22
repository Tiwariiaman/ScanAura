package com.scanaura.activity.scratch.dto;

import com.scanaura.activity.scratch.entity.ScratchPlay;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ScratchPlayReportResponse {
    private UUID id;
    private String customerName;
    private String mobileNumber;
    private String rewardName;
    private String couponCode;
    private ScratchPlay.PlayStatus status;
    private LocalDateTime playedAt;
    private LocalDateTime redeemedAt;
}
