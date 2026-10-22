package com.scanaura.activity.loyalty.repository;

import com.scanaura.activity.loyalty.entity.LoyaltyVisitQr;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;
import java.util.UUID;

public interface LoyaltyVisitQrRepository
        extends JpaRepository<LoyaltyVisitQr, UUID> {

    Optional<LoyaltyVisitQr> findByQrToken(UUID qrToken);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<LoyaltyVisitQr> findByQrTokenAndBusinessId(
            UUID qrToken,
            UUID businessId
    );

    Optional<LoyaltyVisitQr> findByIdAndBusinessId(
            UUID id,
            UUID businessId
    );

    boolean existsByQrToken(UUID qrToken);
}