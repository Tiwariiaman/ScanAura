package com.scanaura.activity.loyalty.repository;

import com.scanaura.activity.loyalty.entity.LoyaltyVisitQr;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LoyaltyVisitQrRepository
        extends JpaRepository<LoyaltyVisitQr, UUID> {

    Optional<LoyaltyVisitQr> findByQrToken(UUID qrToken);

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