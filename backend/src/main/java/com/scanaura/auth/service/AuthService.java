package com.scanaura.auth.service;

import com.scanaura.auth.dto.*;

public interface AuthService {

    RegisterResponse register(
            RegisterRequest request
    );

    LoginResponse login(
            LoginRequest request
    );

    AdminRegisterResponse registerAdmin(
            AdminRegisterRequest request
    );

    void verifyEmail(
            String token
    );

    void resendVerificationEmail(
            String email
    );

    void forgotPassword(
            String email
    );

    void resetPassword(
            ResetPasswordRequest request
    );
}