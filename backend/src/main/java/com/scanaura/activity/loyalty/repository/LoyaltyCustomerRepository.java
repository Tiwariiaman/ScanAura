package com.scanaura.activity.loyalty.repository;

import com.scanaura.activity.loyalty.entity.LoyaltyCustomer;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface LoyaltyCustomerRepository
        extends JpaRepository<LoyaltyCustomer, UUID> {

    java.util.List<LoyaltyCustomer> findAllByBusinessIdOrderByCustomerNameAsc(UUID businessId);

    Optional<LoyaltyCustomer> findByBusinessIdAndMobileNumber(
            UUID businessId,
            String mobileNumber
    );

    boolean existsByBusinessIdAndMobileNumber(
            UUID businessId,
            String mobileNumber
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @   Query("""
    SELECT c
    FROM LoyaltyCustomer c
    WHERE c.id = :customerId
      AND c.businessId = :businessId
    """)
    Optional<LoyaltyCustomer> findByIdAndBusinessIdForUpdate(
            @Param("customerId") UUID customerId,
            @Param("businessId") UUID businessId
    );
}