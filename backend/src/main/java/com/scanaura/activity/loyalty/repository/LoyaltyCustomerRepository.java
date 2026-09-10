package com.scanaura.activity.loyalty.repository;

import com.scanaura.activity.loyalty.entity.LoyaltyCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LoyaltyCustomerRepository
        extends JpaRepository<LoyaltyCustomer, UUID> {

    Optional<LoyaltyCustomer> findByBusinessIdAndMobileNumber(
            UUID businessId,
            String mobileNumber
    );

    boolean existsByBusinessIdAndMobileNumber(
            UUID businessId,
            String mobileNumber
    );
}