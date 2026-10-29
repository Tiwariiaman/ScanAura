package com.scanaura.referral.controller;

import com.scanaura.common.response.ApiResponse;
import com.scanaura.referral.dto.CreateReferralPartnerRequest;
import com.scanaura.referral.dto.ReferralPartnerResponse;
import com.scanaura.referral.dto.ReferralPartnerStatusRequest;
import com.scanaura.referral.service.ReferralPartnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/referral-partners")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminReferralPartnerController {
    private final ReferralPartnerService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReferralPartnerResponse>>> list() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Referral partners loaded", service.list()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReferralPartnerResponse>> create(@Valid @RequestBody CreateReferralPartnerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse<>(true, "Referral partner created", service.create(request)));
    }

    @PatchMapping("/{partnerId}/status")
    public ResponseEntity<ApiResponse<ReferralPartnerResponse>> setStatus(@PathVariable UUID partnerId,
            @Valid @RequestBody ReferralPartnerStatusRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Referral partner status updated",
            service.setActive(partnerId, request.getActive())));
    }
}
