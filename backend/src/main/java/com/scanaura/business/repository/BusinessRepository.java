package com.scanaura.business.repository;

import com.scanaura.auth.entity.User;
import com.scanaura.business.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BusinessRepository extends JpaRepository<Business, UUID> {

    Optional<Business> findByOwner(User owner);

    boolean existsByOwner(User owner);

    Optional<Business> findByQrSlug(String qrSlug);

    long countByActiveTrue();

    long countByActiveFalse();

    List<Business> findAllByOrderByCreatedAtDesc();

    List<Business> findByBusinessNameContainingIgnoreCase(String businessName);

}