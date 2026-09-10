package com.scanaura.activity.loyalty.repository;

import com.scanaura.activity.loyalty.entity.LoyaltySettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LoyaltySettingsRepository
        extends JpaRepository<LoyaltySettings, UUID> {

    Optional<LoyaltySettings> findByBusinessId(UUID businessId);

    boolean existsByBusinessId(UUID businessId);
}