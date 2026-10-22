package com.scanaura.activity.scratch.repository;

import com.scanaura.activity.scratch.entity.ScratchReward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ScratchRewardRepository extends JpaRepository<ScratchReward, UUID> {
    List<ScratchReward> findAllByCardIdOrderByDisplayOrderAsc(UUID cardId);
    void deleteAllByCardId(UUID cardId);
}
