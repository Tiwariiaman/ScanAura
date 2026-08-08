package com.scanaura.admin.controller;

import com.scanaura.admin.dto.DashboardResponse;
import com.scanaura.admin.service.AdminService;
import com.scanaura.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminService adminService;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponse>> dashboard() {

        return ResponseEntity.ok(

                new ApiResponse<>(

                        true,

                        "Dashboard fetched successfully.",

                        adminService.getDashboard()

                )

        );

    }

}