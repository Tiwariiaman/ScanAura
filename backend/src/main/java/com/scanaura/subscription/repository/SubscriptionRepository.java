package com.scanaura.subscription.repository;



import com.scanaura.business.entity.Business;
import com.scanaura.common.enums.SubscriptionStatus;
import com.scanaura.subscription.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    Optional<Subscription> findByBusiness(Business business);

    List<Subscription> findByStatus(SubscriptionStatus status);

    Optional<Subscription> findById(UUID id);

    long countByStatus(SubscriptionStatus status);


}