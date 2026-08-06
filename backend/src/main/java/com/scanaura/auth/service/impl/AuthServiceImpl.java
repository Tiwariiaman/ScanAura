package com.scanaura.auth.service.impl;

import com.scanaura.auth.dto.LoginRequest;
import com.scanaura.auth.dto.LoginResponse;
import com.scanaura.auth.dto.RegisterRequest;
import com.scanaura.auth.dto.RegisterResponse;
import com.scanaura.auth.entity.User;
import com.scanaura.common.exception.BusinessException;
import com.scanaura.auth.repository.UserRepository;
import com.scanaura.auth.service.AuthService;
import com.scanaura.common.constants.AppConstants;
import com.scanaura.common.enums.UserRole;
import com.scanaura.common.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;


    // Register
    @Override
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(AppConstants.EMAIL_ALREADY_EXISTS);
        }

        // Check if mobile already exists
        if (userRepository.existsByMobile(request.getMobile())) {
            throw new BusinessException(AppConstants.MOBILE_ALREADY_EXISTS);
        }

        // Create new user
        final User user = new User();

        // Basic Information
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setMobile(request.getMobile());

        // Encrypt password before saving
        final String encryptedPassword =
                passwordEncoder.encode(request.getPassword());

        user.setPassword(encryptedPassword);

        // Default values
        user.setRole(UserRole.BUSINESS_OWNER);
        user.setVerified(false);
        user.setActive(true);

        // Save user
        final User savedUser = userRepository.save(user);

        // Return response
        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getMobile()
        );

    }

    // Login
    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new BusinessException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail());

        return new LoginResponse(
                token,
                "Bearer",
                86400000L
        );
    }

}