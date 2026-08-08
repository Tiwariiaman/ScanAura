package com.scanaura.subscription.repository;


import com.scanaura.business.entity.Business;
import com.scanaura.common.enums.RequestStatus;
import com.scanaura.subscription.entity.SubscriptionRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRequestRepository
        extends JpaRepository<SubscriptionRequest, UUID> {

    List<SubscriptionRequest> findByStatus(
            RequestStatus status
    );

    List<SubscriptionRequest> findByBusiness(
            Business business
    );

    Optional<SubscriptionRequest>
    findByBusinessAndStatus(
            Business business,
            RequestStatus status
    );

    long countByStatus(RequestStatus status);

}
