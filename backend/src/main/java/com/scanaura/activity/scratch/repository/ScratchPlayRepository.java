package com.scanaura.activity.scratch.repository;

import com.scanaura.activity.scratch.entity.ScratchPlay;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScratchPlayRepository extends JpaRepository<ScratchPlay, UUID> {

    List<ScratchPlay> findTop100ByBusinessIdOrderByPlayedAtDesc(UUID businessId);

    Optional<ScratchPlay> findTopByMobileNumberOrderByPlayedAtDesc(String mobileNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from ScratchPlay p where p.id = :playId and p.businessId = :businessId")
    Optional<ScratchPlay> findPlayForUpdate(@Param("playId") UUID playId,
                                           @Param("businessId") UUID businessId);
}
