package com.scanaura.activity.loyalty.repository;

import com.scanaura.activity.loyalty.entity.LoyaltyClaim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LoyaltyClaimRepository
        extends JpaRepository<LoyaltyClaim, UUID> {

    Optional<LoyaltyClaim> findByQrToken(UUID qrToken);

    Optional<LoyaltyClaim> findByQrTokenAndBusinessId(
            UUID qrToken,
            UUID businessId
    );

    Optional<LoyaltyClaim> findByIdAndBusinessId(
            UUID id,
            UUID businessId
    );

    boolean existsByQrToken(UUID qrToken);
}