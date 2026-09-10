package com.scanaura.activity.loyalty.repository;

import com.scanaura.activity.loyalty.entity.LoyaltyReward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoyaltyRewardRepository
        extends JpaRepository<LoyaltyReward, UUID> {

    List<LoyaltyReward> findAllByBusinessIdOrderByPointsRequiredAsc(
            UUID businessId
    );

    List<LoyaltyReward> findAllByBusinessIdAndActiveTrueOrderByPointsRequiredAsc(
            UUID businessId
    );

    Optional<LoyaltyReward> findByIdAndBusinessId(
            UUID id,
            UUID businessId
    );

    boolean existsByIdAndBusinessId(
            UUID id,
            UUID businessId
    );
}