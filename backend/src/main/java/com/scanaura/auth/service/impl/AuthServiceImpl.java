package com.scanaura.auth.service.impl;

import com.scanaura.auth.dto.*;
import com.scanaura.auth.entity.EmailVerificationToken;
import com.scanaura.auth.entity.PasswordResetToken;
import com.scanaura.auth.entity.User;
import com.scanaura.auth.repository.EmailVerificationTokenRepository;
import com.scanaura.auth.repository.PasswordResetTokenRepository;
import com.scanaura.auth.service.EmailService;
import com.scanaura.common.exception.BusinessException;
import com.scanaura.auth.repository.UserRepository;
import com.scanaura.auth.service.AuthService;
import com.scanaura.common.constants.AppConstants;
import com.scanaura.common.enums.UserRole;
import com.scanaura.common.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final EmailVerificationTokenRepository
            emailVerificationTokenRepository;

    private final EmailService emailService;

    private final PasswordResetTokenRepository
            passwordResetTokenRepository;

    @Value("${scanaura.frontend-url}")
    private String frontendUrl;


    // Register
    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        final String normalizedEmail =
                request.getEmail()
                        .trim()
                        .toLowerCase();

        if (userRepository.existsByEmail(
                normalizedEmail
        )) {
            throw new BusinessException(
                    AppConstants.EMAIL_ALREADY_EXISTS
            );
        }

        // Check if mobile already exists
        if (userRepository.existsByMobile(request.getMobile())) {
            throw new BusinessException(AppConstants.MOBILE_ALREADY_EXISTS);
        }

        // Create new user
        final User user = new User();

        // Basic Information
        user.setFullName(request.getFullName());
        user.setEmail(
                normalizedEmail
        );
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
        final User savedUser =
                userRepository.save(user);

// Remove any previous verification token
// belonging to this user.
        emailVerificationTokenRepository
                .deleteByUser(savedUser);

// Generate a secure random token.
        final String verificationToken =
                UUID.randomUUID()
                        .toString();

// Create verification token.
        EmailVerificationToken token =
                new EmailVerificationToken();

        token.setUser(savedUser);
        token.setToken(
                verificationToken
        );
        token.setExpiresAt(
                LocalDateTime.now()
                        .plusHours(24)
        );
        token.setUsed(false);

// Save token.
        emailVerificationTokenRepository
                .save(token);

// Build verification URL.
        final String verificationUrl =
                frontendUrl
                        + "/verify-email?token="
                        + verificationToken;

// Send verification email.
        emailService.sendVerificationEmail(
                savedUser.getEmail(),
                savedUser.getFullName(),
                verificationUrl
        );
// Return response.
        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getMobile()
        );

    }

    // Login
    // Login
    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new BusinessException(
                                "Invalid email or password"
                        )
                );

        // Check password first.
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new BusinessException(
                    "Invalid email or password"
            );
        }

        // Email verification is required
        // before the user can access the application.
        if (!Boolean.TRUE.equals(
                user.getVerified()
        )) {
            throw new BusinessException(
                    "Please verify your email before signing in."
            );
        }

        // Account must be active.
        if (!Boolean.TRUE.equals(
                user.getActive()
        )) {
            throw new BusinessException(
                    "Your account is inactive."
            );
        }

        // Account must not be deleted.
        if (Boolean.TRUE.equals(
                user.getDeleted()
        )) {
            throw new BusinessException(
                    "Your account is unavailable."
            );
        }

        String token =
                jwtService.generateToken(
                        user.getEmail(),
                        user.getRole().name()
                );

        return new LoginResponse(
                token,
                "Bearer",
                86400000L
        );
    }

    //Admin Register
    @Override
    public AdminRegisterResponse registerAdmin(
            AdminRegisterRequest request
    ) {

        if (userRepository.existsByEmail(
                request.getEmail()
        )) {
            throw new BusinessException(
                    AppConstants.EMAIL_ALREADY_EXISTS
            );
        }

        if (userRepository.existsByMobile(
                request.getMobile()
        )) {
            throw new BusinessException(
                    AppConstants.MOBILE_ALREADY_EXISTS
            );
        }

        User user = new User();

        user.setFullName(
                request.getFullName()
        );

        user.setEmail(
                request.getEmail()
        );

        user.setMobile(
                request.getMobile()
        );

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        user.setRole(
                UserRole.ADMIN
        );

        user.setVerified(true);
        user.setActive(true);

        User savedUser =
                userRepository.save(user);

        return new AdminRegisterResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getMobile(),
                savedUser.getRole().name()
        );
    }

    @Override
    public void verifyEmail(String token) {

        if (token == null ||
                token.trim().isEmpty()) {
            throw new BusinessException(
                    "Verification token is required."
            );
        }

        EmailVerificationToken verificationToken =
                emailVerificationTokenRepository
                        .findByToken(token.trim())
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Invalid verification link."
                                )
                        );

        if (Boolean.TRUE.equals(
                verificationToken.getUsed()
        )) {
            throw new BusinessException(
                    "This verification link has already been used."
            );
        }

        if (verificationToken
                .getExpiresAt()
                .isBefore(
                        LocalDateTime.now()
                )) {

            throw new BusinessException(
                    "This verification link has expired."
            );
        }

        User user =
                verificationToken.getUser();

        user.setVerified(true);

        userRepository.save(user);

        verificationToken.setUsed(true);

        emailVerificationTokenRepository.save(
                verificationToken
        );
    }

    @Override
    @Transactional
    public void resendVerificationEmail(
            String email
    ) {

        if (email == null ||
                email.trim().isEmpty()) {
            return;
        }

        final String normalizedEmail =
                email.trim().toLowerCase();

        final User user =
                userRepository
                        .findByEmail(normalizedEmail)
                        .orElse(null);

        /*
         * Do not reveal whether an email
         * exists in the database.
         */
        if (user == null) {
            return;
        }

        /*
         * Already verified.
         * No new verification email is needed.
         */
        if (Boolean.TRUE.equals(
                user.getVerified()
        )) {
            return;
        }

        final LocalDateTime now =
                LocalDateTime.now();

        /*
         * Find the existing token.
         */
        final EmailVerificationToken verificationToken =
                emailVerificationTokenRepository
                        .findByUser(user)
                        .orElse(null);

        /*
         * 60-second resend cooldown.
         *
         * We use updatedAt because BaseEntity
         * updates this automatically whenever
         * the token is saved.
         */
        if (verificationToken != null &&
                verificationToken.getUpdatedAt() != null) {

            final long secondsSinceLastUpdate =
                    Duration.between(
                            verificationToken
                                    .getUpdatedAt(),
                            now
                    ).getSeconds();

            if (secondsSinceLastUpdate < 60) {
                return;
            }
        }

        final String newVerificationToken =
                UUID.randomUUID()
                        .toString();

        /*
         * Reuse the existing token row when possible.
         * This avoids creating multiple token
         * records for the same user.
         */
        if (verificationToken != null) {

            verificationToken.setToken(
                    newVerificationToken
            );

            verificationToken.setExpiresAt(
                    now.plusHours(24)
            );

            verificationToken.setUsed(false);

            emailVerificationTokenRepository.save(
                    verificationToken
            );

        } else {

            EmailVerificationToken newToken =
                    new EmailVerificationToken();

            newToken.setUser(user);

            newToken.setToken(
                    newVerificationToken
            );

            newToken.setExpiresAt(
                    now.plusHours(24)
            );

            newToken.setUsed(false);

            emailVerificationTokenRepository.save(
                    newToken
            );
        }

        final String verificationUrl =
                "http://localhost:8080/api/v1/auth/verify-email?token="
                        + newVerificationToken;

        emailService.sendVerificationEmail(
                user.getEmail(),
                user.getFullName(),
                verificationUrl
        );
    }

    @Override
    @Transactional
    public void forgotPassword(
            String email
    ) {

        if (email == null ||
                email.trim().isEmpty()) {
            return;
        }

        final String normalizedEmail =
                email.trim().toLowerCase();

        final User user =
                userRepository
                        .findByEmail(normalizedEmail)
                        .orElse(null);

        /*
         * Never reveal whether the email
         * exists in our database.
         */
        if (user == null) {
            return;
        }

        /*
         * Remove any previous reset token.
         */
        passwordResetTokenRepository
                .deleteByUser(user);

        final String resetToken =
                UUID.randomUUID()
                        .toString();

        final PasswordResetToken token =
                new PasswordResetToken();

        token.setUser(user);
        token.setToken(resetToken);
        token.setExpiresAt(
                LocalDateTime.now()
                        .plusMinutes(30)
        );
        token.setUsed(false);

        passwordResetTokenRepository
                .save(token);

        final String resetUrl =
                frontendUrl
                        + "/reset-password?token="
                        + resetToken;

        emailService.sendPasswordResetEmail(
                user.getEmail(),
                user.getFullName(),
                resetUrl
        );
    }

    @Override
    @Transactional
    public void resetPassword(
            ResetPasswordRequest request
    ) {

        final String token =
                request.getToken()
                        .trim();

        final PasswordResetToken resetToken =
                passwordResetTokenRepository
                        .findByToken(token)
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Invalid password reset link."
                                )
                        );

        if (Boolean.TRUE.equals(
                resetToken.getUsed()
        )) {
            throw new BusinessException(
                    "This password reset link has already been used."
            );
        }

        if (resetToken.getExpiresAt()
                .isBefore(
                        LocalDateTime.now()
                )) {
            throw new BusinessException(
                    "This password reset link has expired."
            );
        }

        final User user =
                resetToken.getUser();

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        userRepository.save(user);

        resetToken.setUsed(true);

        passwordResetTokenRepository.save(
                resetToken
        );
    }

}