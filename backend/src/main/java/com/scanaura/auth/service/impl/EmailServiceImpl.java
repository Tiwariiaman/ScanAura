package com.scanaura.auth.service.impl;

import com.scanaura.auth.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${scanaura.logo-url}")
    private String logoUrl;

    @Override
    public void sendVerificationEmail(
            String recipientEmail,
            String recipientName,
            String verificationUrl
    ) {
        Context context = new Context();
        context.setVariable("recipientName", recipientName);
        context.setVariable("verificationUrl", verificationUrl);
        context.setVariable("logoUrl", logoUrl);

        String html = templateEngine.process(
                "email/verification-email",
                context
        );

        sendHtmlEmail(
                recipientEmail,
                "Verify your ScanAura email",
                html
        );
    }

    @Override
    public void sendPasswordResetEmail(
            String recipientEmail,
            String recipientName,
            String resetUrl
    ) {
        Context context = new Context();
        context.setVariable("recipientName", recipientName);
        context.setVariable("resetUrl", resetUrl);
        context.setVariable("logoUrl", logoUrl);

        String html = templateEngine.process(
                "email/password-reset-email",
                context
        );

        sendHtmlEmail(
                recipientEmail,
                "Reset your ScanAura password",
                html
        );
    }

    private void sendHtmlEmail(
            String recipientEmail,
            String subject,
            String html
    ) {
        try {
            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true,
                            "UTF-8"
                    );

            helper.setFrom(
                    "ScanAura <" + fromEmail + ">"
            );

            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new IllegalStateException(
                    "Unable to send email.",
                    e
            );
        }
    }
}