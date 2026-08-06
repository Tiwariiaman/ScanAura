package com.scanaura.business.controller;

import com.scanaura.business.dto.BusinessRequest;
import com.scanaura.business.dto.BusinessResponse;
import com.scanaura.business.service.BusinessService;
import com.scanaura.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/business")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;

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
}