package com.scanaura.activity.loyalty.repository;

import com.scanaura.activity.loyalty.entity.LoyaltyTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoyaltyTransactionRepository
        extends JpaRepository<LoyaltyTransaction, UUID> {

    Optional<LoyaltyTransaction>
    findFirstByBusinessIdAndCustomerIdAndTransactionTypeAndCreatedAtBetween(
            UUID businessId,
            UUID customerId,
            LoyaltyTransaction.TransactionType transactionType,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay
    );

    List<LoyaltyTransaction>
    findAllByBusinessIdAndCustomerIdOrderByCreatedAtDesc(
            UUID businessId,
            UUID customerId
    );
}