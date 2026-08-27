package com.scanaura.auth.service;

public interface EmailService {

    void sendVerificationEmail(
            String recipientEmail,
            String recipientName,
            String verificationUrl
    );

    void sendPasswordResetEmail(
            String recipientEmail,
            String recipientName,
            String resetUrl
    );
}