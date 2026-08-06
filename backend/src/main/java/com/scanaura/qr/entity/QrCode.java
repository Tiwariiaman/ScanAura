package com.scanaura.qr.entity;

import com.scanaura.business.entity.Business;
import com.scanaura.common.entity.BaseEntity;
import com.scanaura.common.enums.QrType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "qr_codes")
@Getter
@Setter
public class QrCode extends BaseEntity {

    @Column(nullable = false, unique = true, length = 30)
    private String qrCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id")
    private Business business;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QrType type = QrType.DIGITAL;

    @Column(nullable = false)
    private Boolean assigned = false;

    @Column(nullable = false)
    private Boolean active = true;

}