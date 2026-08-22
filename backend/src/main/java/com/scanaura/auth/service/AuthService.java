package com.scanaura.auth.service;

import com.scanaura.auth.dto.*;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
    AdminRegisterResponse registerAdmin(
            AdminRegisterRequest request
    );
}