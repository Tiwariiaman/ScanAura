package com.scanaura.auth.entity;

import com.scanaura.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "email_verification_tokens",
        indexes = {
                @Index(
                        name = "idx_email_verification_token",
                        columnList = "token",
                        unique = true
                )
        }
)
public class EmailVerificationToken
        extends BaseEntity {

    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true
    )
    private User user;

    @Column(
            nullable = false,
            unique = true,
            length = 100
    )
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Boolean used = false;
}