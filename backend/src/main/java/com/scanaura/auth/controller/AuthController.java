package com.scanaura.auth.controller;

import com.scanaura.auth.dto.*;
import com.scanaura.auth.service.AuthService;
import com.scanaura.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.scanaura.common.constants.AppConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Register
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        final RegisterResponse response =
                authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                AppConstants.USER_REGISTERED,
                                response
                        )
                );
    }


    // Login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Login successful.",
                        response
                )
        );
    }

    //Admin Register
    @PostMapping("/register-admin")
    public ResponseEntity<ApiResponse<AdminRegisterResponse>> registerAdmin(
            @Valid @RequestBody AdminRegisterRequest request
    ) {

        AdminRegisterResponse response =
                authService.registerAdmin(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Admin registered successfully.",
                                response
                        )
                );
    }

    // Verify Email
    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse<String>> verifyEmail(
            @RequestParam String token
    ) {

        authService.verifyEmail(token);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Email verified successfully.",
                        "Your email has been verified."
                )
        );
    }

    // Resend Verification Email
    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<String>> resendVerificationEmail(
            @Valid @RequestBody ResendVerificationRequest request
    ) {

        authService.resendVerificationEmail(
                request.getEmail()
        );

        /*
         * Always return the same response.
         *
         * This prevents revealing whether
         * an email address exists in the system.
         */
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "If the account exists and is not verified, "
                                + "a verification email has been sent.",
                        null
                )
        );
    }

    // Forgot Password
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {

        authService.forgotPassword(
                request.getEmail()
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "If the account exists, "
                                + "password reset instructions "
                                + "have been sent.",
                        null
                )
        );
    }


    // Reset Password
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {

        authService.resetPassword(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Password reset successfully.",
                        "Your password has been updated."
                )
        );
    }

}