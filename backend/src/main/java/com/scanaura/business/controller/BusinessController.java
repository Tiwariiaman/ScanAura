package com.scanaura.business.controller;

import com.scanaura.business.dto.BusinessDashboardResponse;
import com.scanaura.business.dto.BusinessRequest;
import com.scanaura.business.dto.BusinessResponse;
import com.scanaura.business.repository.BusinessRepository;
import com.scanaura.business.service.BusinessService;
import com.scanaura.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/business")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;
    private final BusinessRepository businessRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<BusinessResponse>> createBusiness(
            @Valid @RequestBody BusinessRequest request
    ) {

        BusinessResponse response = businessService.createBusiness(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        "Business created successfully.",
                        response
                ));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<BusinessResponse>> getMyBusiness() {

        BusinessResponse response = businessService.getMyBusiness();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Business fetched successfully.",
                        response
                )
        );
    }

    @PutMapping
    public ResponseEntity<ApiResponse<BusinessResponse>> updateBusiness(
            @Valid @RequestBody BusinessRequest request
    ) {

        BusinessResponse response = businessService.updateBusiness(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Business updated successfully.",
                        response
                )
        );
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> deleteBusiness() {

        businessService.deleteBusiness();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Business deleted successfully.",
                        "Deleted"
                )
        );
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<BusinessDashboardResponse>> getMyDashboard() {

        BusinessDashboardResponse response =
                businessService.getMyDashboard();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Business dashboard fetched successfully.",
                        response
                )
        );
    }

    private static final Set<String> RESERVED_SLUGS = Set.of(
            "www", "api", "admin", "app", "mail",
            "support", "help", "blog", "docs",
            "dashboard", "login", "register",
            "signup", "pricing", "about",
            "contact", "status", "cdn", "static", "business"
    );

    @GetMapping("/public-slug/check")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkPublicSlug(
            @RequestParam String slug
    ) {
        String normalized = slug.trim().toLowerCase(Locale.ROOT);

        boolean valid = normalized.matches(
                "^[a-z0-9]+(?:-[a-z0-9]+)*$"
        ) && normalized.length() >= 3
                && normalized.length() <= 50;

        boolean available = valid
                && !RESERVED_SLUGS.contains(normalized)
                && !businessRepository.existsByPublicSlugIgnoreCase(normalized);

        Map<String, Object> data = Map.of(
                "available", available,
                "slug", normalized,
                "valid", valid
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        available
                                ? "Business link is available."
                                : "Business link is unavailable.",
                        data
                )
        );
    }
}