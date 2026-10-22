package com.scanaura.activity.scratch.repository;

import com.scanaura.activity.scratch.entity.ScratchCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ScratchCardRepository extends JpaRepository<ScratchCard, UUID> {
    Optional<ScratchCard> findByBusinessId(UUID businessId);
}
