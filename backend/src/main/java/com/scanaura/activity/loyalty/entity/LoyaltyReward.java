package com.scanaura.activity.loyalty.entity;

import com.scanaura.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "activity_loyalty_rewards")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyReward extends BaseEntity {

    @Column(name = "business_id", nullable = false)
    private UUID businessId;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(name = "points_required", nullable = false)
    private Integer pointsRequired;

    @Column(
            name = "reward_amount",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal rewardAmount;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;
}