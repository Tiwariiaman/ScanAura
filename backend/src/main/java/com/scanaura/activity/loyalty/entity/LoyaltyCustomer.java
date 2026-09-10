package com.scanaura.activity.loyalty.entity;

import com.scanaura.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "activity_loyalty_customers",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_activity_loyalty_customer_business_mobile",
                        columnNames = {
                                "business_id",
                                "mobile_number"
                        }
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyCustomer extends BaseEntity {

    @Column(name = "business_id", nullable = false)
    private UUID businessId;

    @Column(
            name = "mobile_number",
            nullable = false,
            length = 20
    )
    private String mobileNumber;

    @Column(
            name = "customer_name",
            nullable = false,
            length = 120
    )
    private String customerName;

    @Builder.Default
    @Column(
            name = "points_balance",
            nullable = false
    )
    private Integer pointsBalance = 0;

    @Builder.Default
    @Column(
            name = "total_points_earned",
            nullable = false
    )
    private Integer totalPointsEarned = 0;

    @Builder.Default
    @Column(
            name = "total_points_redeemed",
            nullable = false
    )
    private Integer totalPointsRedeemed = 0;
}