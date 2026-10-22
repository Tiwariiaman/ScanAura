package com.scanaura.activity.scratch.controller;

import com.scanaura.activity.scratch.dto.ScratchApiResponse;
import com.scanaura.activity.scratch.dto.ScratchPlayRequest;
import com.scanaura.activity.scratch.dto.ScratchPlayResponse;
import com.scanaura.activity.scratch.dto.ScratchPublicStatusResponse;
import com.scanaura.activity.scratch.service.ScratchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/public/scratch")
@RequiredArgsConstructor
public class PublicScratchController {

    private final ScratchService scratchService;

    @GetMapping
    public ResponseEntity<ScratchApiResponse<ScratchPublicStatusResponse>> getStatus(
            @RequestParam UUID businessId) {
        return ResponseEntity.ok(ScratchApiResponse.success("Scratch Card status loaded.",
                scratchService.getPublicStatus(businessId)));
    }

    @PostMapping("/play")
    public ResponseEntity<ScratchApiResponse<ScratchPlayResponse>> play(
            @RequestParam UUID businessId,
            @Valid @RequestBody ScratchPlayRequest request) {
        return ResponseEntity.ok(ScratchApiResponse.success("Scratch completed. Congratulations!",
                scratchService.play(businessId, request)));
    }
}
