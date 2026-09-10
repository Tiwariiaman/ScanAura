package com.scanaura.activity.loyalty.entity;

import com.scanaura.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
        name = "activity_loyalty_claims",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_activity_loyalty_claim_qr_token",
                        columnNames = "qr_token"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyClaim extends BaseEntity {

    @Column(name = "business_id", nullable = false)
    private UUID businessId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "reward_id", nullable = false)
    private UUID rewardId;

    @Column(name = "points_used", nullable = false)
    private Integer pointsUsed;

    @Column(
            name = "reward_amount",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal rewardAmount;

    @Column(
            name = "qr_token",
            nullable = false,
            unique = true
    )
    private UUID qrToken;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    @Builder.Default
    private ClaimStatus status = ClaimStatus.PENDING;

    @Column(name = "granted_at")
    private java.time.LocalDateTime grantedAt;

    public enum ClaimStatus {
        PENDING,
        GRANTED,
        CANCELLED
    }
}