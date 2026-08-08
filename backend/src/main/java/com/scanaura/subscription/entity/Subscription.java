package com.scanaura.subscription.entity;

import com.scanaura.auth.entity.User;
import com.scanaura.business.entity.Business;
import com.scanaura.common.entity.BaseEntity;
import com.scanaura.common.enums.BillingCycle;
import com.scanaura.common.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false, unique = true)
    private Business business;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillingCycle billingCycle;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

//    @Column(length = 500)
//    private String paymentScreenshotUrl;
//
//    @Column(length = 100)
//    private String transactionId;



//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "approved_by")
//    private User approvedBy;

    @Column(nullable = false)
    @Builder.Default
    private Integer aiImportUsed = 0;


}
