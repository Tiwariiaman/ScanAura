package com.scanaura.activity.loyalty.entity;

import com.scanaura.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "activity_loyalty_settings",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_activity_loyalty_settings_business",
                        columnNames = "business_id"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltySettings extends BaseEntity {

    @Column(name = "business_id", nullable = false)
    private UUID businessId;

    @Builder.Default
    @Column(nullable = false)
    private Boolean enabled = false;

    @Builder.Default
    @Column(name = "points_per_visit", nullable = false)
    private Integer pointsPerVisit = 10;
}