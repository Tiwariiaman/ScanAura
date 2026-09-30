package com.scanaura.auth.service.impl;

import com.scanaura.auth.dto.*;
import com.scanaura.auth.entity.EmailVerificationToken;
import com.scanaura.auth.entity.PasswordResetToken;
import com.scanaura.auth.entity.RefreshToken;
import com.scanaura.auth.entity.User;
import com.scanaura.auth.repository.EmailVerificationTokenRepository;
import com.scanaura.auth.repository.PasswordResetTokenRepository;
import com.scanaura.auth.repository.RefreshTokenRepository;
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

    private final RefreshTokenRepository refreshTokenRepository;


    // =========================================================
    // REGISTER
    // =========================================================

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        /*
         * Email is now OPTIONAL.
         *
         * If email is empty/null, store NULL in database.
         * If email is provided, normalize it.
         */
        String normalizedEmail = null;

        if (request.getEmail() != null &&
                !request.getEmail().trim().isEmpty()) {

            normalizedEmail =
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
        }

        // Check if mobile already exists
        if (userRepository.existsByMobile(
                request.getMobile()
        )) {
            throw new BusinessException(
                    AppConstants.MOBILE_ALREADY_EXISTS
            );
        }

        // Create new user
        final User user = new User();

        // Basic Information
        user.setFullName(
                request.getFullName()
        );

        user.setEmail(
                normalizedEmail
        );

        user.setMobile(
                request.getMobile()
        );

        // Encrypt password before saving
        final String encryptedPassword =
                passwordEncoder.encode(
                        request.getPassword()
                );

        user.setPassword(
                encryptedPassword
        );

        // Default values
        user.setRole(
                UserRole.BUSINESS_OWNER
        );

        /*
         * New users do NOT need email verification
         * to use ScanAura.
         *
         * Keep this false because email can still be
         * verified later from the account.
         */
        user.setVerified(false);

        user.setActive(true);

        // Save user
        final User savedUser =
                userRepository.save(user);

        /*
         * IMPORTANT:
         *
         * We intentionally DO NOT create a verification
         * token here.
         *
         * We also DO NOT send a verification email here.
         *
         * Verification will happen only when the user
         * explicitly requests it from their account.
         */

        // Return response
        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getMobile()
        );
    }


    // =========================================================
    // LOGIN
    // =========================================================

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {

        /*
         * Login can now use:
         *
         * 1. Email
         * OR
         * 2. Mobile number
         */
        final String identifier =
                request.getIdentifier()
                        .trim();

        User user;

        /*
         * If identifier contains "@", treat it as email.
         * Otherwise treat it as mobile.
         */
        if (identifier.contains("@")) {

            user =
                    userRepository
                            .findByEmail(
                                    identifier
                                            .toLowerCase()
                            )
                            .orElseThrow(() ->
                                    new BusinessException(
                                            "Account not found. Please register."
                                    )
                            );

        } else {

            user =
                    userRepository
                            .findByMobile(
                                    identifier
                            )
                            .orElseThrow(() ->
                                    new BusinessException(
                                            "Account not found. Please register."
                                    )
                            );
        }

        // Check password
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {

            throw new BusinessException(
                    "Incorrect email/mobile or password."
            );
        }

        /*
         * EMAIL VERIFICATION IS NO LONGER REQUIRED
         * FOR LOGIN.
         *
         * Existing users who are verified continue working.
         * New users can login immediately.
         */

        // Check account active
        if (!Boolean.TRUE.equals(
                user.getActive()
        )) {

            throw new BusinessException(
                    "Your account is inactive."
            );
        }

        // Check deleted account
        if (Boolean.TRUE.equals(
                user.getDeleted()
        )) {

            throw new BusinessException(
                    "Your account is unavailable."
            );
        }

        /*
         * JWT identity:
         *
         * Existing users:
         *     email
         *
         * New users without email:
         *     mobile
         *
         * This keeps existing JWT behaviour compatible.
         */
        final String jwtIdentifier =
                user.getEmail() != null &&
                        !user.getEmail().trim().isEmpty()
                        ? user.getEmail()
                        : user.getMobile();

        String accessToken =
                jwtService.generateToken(
                        jwtIdentifier,
                        user.getRole().name()
                );

        String refreshToken =
                createRefreshToken(user);

        return new LoginResponse(
                accessToken,
                refreshToken,
                "Bearer",
                86400000L
        );
    }


    // =========================================================
    // ADMIN REGISTER
    // =========================================================

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


    // =========================================================
    // VERIFY EMAIL
    // =========================================================

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
                        .findByToken(
                                token.trim()
                        )
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


    // =========================================================
    // RESEND VERIFICATION EMAIL
    // =========================================================

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
                        .findByEmail(
                                normalizedEmail
                        )
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
         * Find existing token.
         */
        final EmailVerificationToken verificationToken =
                emailVerificationTokenRepository
                        .findByUser(user)
                        .orElse(null);

        /*
         * 60-second resend cooldown.
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
         * Reuse existing token row where possible.
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
                frontendUrl
                        + "/#/verify-email?token="
                        + newVerificationToken;

        emailService.sendVerificationEmail(
                user.getEmail(),
                user.getFullName(),
                verificationUrl
        );
    }


    // =========================================================
    // FORGOT PASSWORD
    // =========================================================

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
                        .findByEmail(
                                normalizedEmail
                        )
                        .orElse(null);

        /*
         * Never reveal whether the email
         * exists in our database.
         */
        if (user == null) {
            return;
        }

        /*
         * Remove previous reset token.
         */
        passwordResetTokenRepository
                .deleteByUser(user);

        final String resetToken =
                UUID.randomUUID()
                        .toString();

        final PasswordResetToken token =
                new PasswordResetToken();

        token.setUser(user);

        token.setToken(
                resetToken
        );

        token.setExpiresAt(
                LocalDateTime.now()
                        .plusMinutes(30)
        );

        token.setUsed(false);

        passwordResetTokenRepository
                .save(token);

        final String resetUrl =
                frontendUrl
                        + "/#/reset-password?token="
                        + resetToken;

        emailService.sendPasswordResetEmail(
                user.getEmail(),
                user.getFullName(),
                resetUrl
        );
    }


    // =========================================================
    // RESET PASSWORD
    // =========================================================

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


    // =========================================================
    // CREATE REFRESH TOKEN
    // =========================================================

    private String createRefreshToken(
            User user
    ) {

        // Remove previous refresh tokens
        // for this user.
        refreshTokenRepository.deleteByUser(
                user
        );

        String token =
                UUID.randomUUID().toString()
                        + UUID.randomUUID().toString();

        RefreshToken refreshToken =
                new RefreshToken();

        refreshToken.setUser(user);

        refreshToken.setToken(token);

        // 30 days
        refreshToken.setExpiresAt(
                LocalDateTime.now()
                        .plusDays(30)
        );

        refreshToken.setRevoked(false);

        refreshTokenRepository.save(
                refreshToken
        );

        return token;
    }


    // =========================================================
    // REFRESH TOKEN
    // =========================================================

    @Override
    @Transactional
    public RefreshTokenResponse refreshToken(
            RefreshTokenRequest request
    ) {

        RefreshToken existingToken =
                refreshTokenRepository
                        .findByToken(
                                request.getRefreshToken()
                        )
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Invalid refresh token."
                                )
                        );

        // Check if manually revoked.
        if (existingToken.isRevoked()) {

            throw new BusinessException(
                    "Refresh token has been revoked."
            );
        }

        // Check expiration.
        if (existingToken.getExpiresAt()
                .isBefore(
                        LocalDateTime.now()
                )) {

            refreshTokenRepository.delete(
                    existingToken
            );

            throw new BusinessException(
                    "Refresh token has expired. Please login again."
            );
        }

        User user =
                existingToken.getUser();

        // User account checks.
        if (!Boolean.TRUE.equals(
                user.getActive()
        )) {

            throw new BusinessException(
                    "Your account is inactive."
            );
        }

        if (Boolean.TRUE.equals(
                user.getDeleted()
        )) {

            throw new BusinessException(
                    "Your account is unavailable."
            );
        }

        /*
         * Existing users:
         *     email
         *
         * New users without email:
         *     mobile
         */
        final String jwtIdentifier =
                user.getEmail() != null &&
                        !user.getEmail().trim().isEmpty()
                        ? user.getEmail()
                        : user.getMobile();

        // Generate new access token.
        String accessToken =
                jwtService.generateToken(
                        jwtIdentifier,
                        user.getRole().name()
                );

        String newRefreshToken =
                UUID.randomUUID().toString()
                        + UUID.randomUUID().toString();

        existingToken.setToken(
                newRefreshToken
        );

        existingToken.setExpiresAt(
                LocalDateTime.now()
                        .plusDays(30)
        );

        existingToken.setRevoked(false);

        refreshTokenRepository.save(
                existingToken
        );

        return new RefreshTokenResponse(
                accessToken,
                newRefreshToken,
                "Bearer",
                86400000L
        );
    }
}