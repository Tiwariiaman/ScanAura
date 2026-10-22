package com.scanaura.activity.loyalty.repository;

import com.scanaura.activity.loyalty.entity.LoyaltyClaim;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;
import java.util.UUID;

public interface LoyaltyClaimRepository
        extends JpaRepository<LoyaltyClaim, UUID> {

    Optional<LoyaltyClaim> findByQrToken(UUID qrToken);

    Optional<LoyaltyClaim> findByIdAndBusinessId(
            UUID id,
            UUID businessId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<LoyaltyClaim> findByQrTokenAndBusinessId(
            UUID qrToken,
            UUID businessId
    );

    java.util.List<LoyaltyClaim>
    findAllByBusinessIdAndCustomerIdAndStatus(
            UUID businessId,
            UUID customerId,
            LoyaltyClaim.ClaimStatus status
    );

    boolean existsByQrToken(UUID qrToken);
}