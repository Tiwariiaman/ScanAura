package com.scanaura.qr.entity;

import com.scanaura.business.entity.Business;
import com.scanaura.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(
        name = "qr_scan_daily",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_qr_scan_daily_business_date",
                        columnNames = {"business_id", "scan_date"}
                )
        }
)
@Getter
@Setter
public class QrScanDaily extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "business_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_qr_scan_daily_business"
            )
    )
    private Business business;

    @Column(name = "scan_date", nullable = false)
    private LocalDate scanDate;

    @Column(name = "scan_count", nullable = false)
    private Long scanCount = 0L;
}