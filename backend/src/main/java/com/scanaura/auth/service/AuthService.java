package com.scanaura.auth.service;

import com.scanaura.auth.dto.LoginRequest;
import com.scanaura.auth.dto.LoginResponse;
import com.scanaura.auth.dto.RegisterRequest;
import com.scanaura.auth.dto.RegisterResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}