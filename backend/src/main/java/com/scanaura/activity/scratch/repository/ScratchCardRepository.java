package com.scanaura.activity.scratch.repository;

import com.scanaura.activity.scratch.entity.ScratchCard;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ScratchCardRepository extends JpaRepository<ScratchCard, UUID> {

    Optional<ScratchCard> findByBusinessId(UUID businessId);

    /** Serialize plays for a business so concurrent requests cannot bypass that business' daily phone limit. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from ScratchCard c where c.businessId = :businessId")
    Optional<ScratchCard> findByBusinessIdForUpdate(@Param("businessId") UUID businessId);
}
