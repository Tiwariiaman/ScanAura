package com.scanaura.activity.scratch.entity;

import com.scanaura.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "activity_scratch_plays")
@Getter
@Setter
@NoArgsConstructor
public class ScratchPlay extends BaseEntity {

    @Column(name = "card_id", nullable = false)
    private UUID cardId;

    @Column(name = "business_id", nullable = false)
    private UUID businessId;

    @Column(name = "reward_id")
    private UUID rewardId;

    @Column(name = "customer_name", nullable = false, length = 120)
    private String customerName;

    @Column(name = "mobile_number", nullable = false, length = 20)
    private String mobileNumber;

    // Snapshot ensures later reward edits never alter a coupon already won.
    @Column(name = "reward_name_snapshot", nullable = false, length = 150)
    private String rewardNameSnapshot;

    @Column(name = "coupon_code", nullable = false, unique = true, length = 40)
    private String couponCode;

    @Column(name = "coupon_token", nullable = false, unique = true)
    private UUID couponToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PlayStatus status = PlayStatus.WON;

    @Column(name = "played_at", nullable = false)
    private LocalDateTime playedAt;

    @Column(name = "redeemed_at")
    private LocalDateTime redeemedAt;

    public enum PlayStatus { WON, REDEEMED }
}
