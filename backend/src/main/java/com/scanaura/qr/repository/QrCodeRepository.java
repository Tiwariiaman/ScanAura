package com.scanaura.qr.repository;

import com.scanaura.business.entity.Business;
import com.scanaura.common.enums.QrType;
import com.scanaura.qr.entity.QrCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QrCodeRepository extends JpaRepository<QrCode, UUID> {

    Optional<QrCode> findByQrCode(String qrCode);

    boolean existsByQrCode(String qrCode);

    List<QrCode> findByBusiness(Business business);

    List<QrCode> findByAssignedFalseAndActiveTrue();

    List<QrCode> findByAssignedFalseAndTypeAndActiveTrue(QrType type);

    long countByAssignedFalseAndActiveTrue();

    long countByAssignedTrue();

    long countByAssignedFalseAndTypeAndActiveTrue(QrType type);

    long countByAssignedTrueAndType(QrType type);

    long countByType(QrType type);

    Optional<QrCode> findByBusinessAndType(
            Business business,
            QrType type
    );

    List<QrCode> findByBusinessOrderByTypeAscCreatedAtAsc(Business business);

}