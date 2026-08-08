package com.scanaura.subscription.entity;

import com.scanaura.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "plans")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Plan extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal yearlyPrice;

    @Column(nullable = false)
    private Integer trialDays;

    @Column(nullable = false)
    private Integer aiImportLimit;

    @Column(nullable = false)
    private Boolean brandedQr;

    @Column(nullable =false)
    private Boolean prioritySupport;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;
}