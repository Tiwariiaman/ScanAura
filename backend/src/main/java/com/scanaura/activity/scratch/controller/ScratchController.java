package com.scanaura.activity.scratch.controller;

import com.scanaura.activity.scratch.dto.*;
import com.scanaura.activity.scratch.service.ScratchService;
import com.scanaura.auth.entity.User;
import com.scanaura.business.entity.Business;
import com.scanaura.business.repository.BusinessRepository;
import com.scanaura.common.exception.BusinessException;
import com.scanaura.common.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/activity/scratch")
@RequiredArgsConstructor
public class ScratchController {

    private final ScratchService scratchService;
    private final BusinessRepository businessRepository;

    @GetMapping
    public ResponseEntity<ScratchApiResponse<ScratchSettingsResponse>> getSettings() {
        return ResponseEntity.ok(ScratchApiResponse.success("Scratch Card settings loaded.",
                scratchService.getSettings(getBusinessId())));
    }

    @PutMapping
    public ResponseEntity<ScratchApiResponse<ScratchSettingsResponse>> updateSettings(
            @Valid @RequestBody ScratchSettingsRequest request) {
        return ResponseEntity.ok(ScratchApiResponse.success("Scratch Card settings saved.",
                scratchService.updateSettings(getBusinessId(), request)));
    }

    @GetMapping("/plays")
    public ResponseEntity<ScratchApiResponse<List<ScratchPlayReportResponse>>> getRecentPlays() {
        return ResponseEntity.ok(ScratchApiResponse.success("Recent Scratch Card plays loaded.",
                scratchService.getRecentPlays(getBusinessId())));
    }

    @PostMapping("/redeem")
    public ResponseEntity<ScratchApiResponse<ScratchPlayReportResponse>> redeem(
            @Valid @RequestBody ScratchRedeemRequest request) {
        return ResponseEntity.ok(ScratchApiResponse.success("Scratch Card reward marked as redeemed.",
                scratchService.redeem(getBusinessId(), request.getPlayId())));
    }

    private UUID getBusinessId() {
        User currentUser = SecurityUtil.getCurrentUser();
        Business business = businessRepository.findByOwner(currentUser)
                .orElseThrow(() -> new BusinessException("Business profile not found for current user."));
        return business.getId();
    }
}
