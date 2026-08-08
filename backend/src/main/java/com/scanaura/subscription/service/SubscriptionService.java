package com.scanaura.subscription.service;

import com.scanaura.business.entity.Business;
import com.scanaura.subscription.dto.*;

import java.util.List;
import java.util.UUID;

public interface SubscriptionService {

    void createTrialSubscription(Business business);

    SubscriptionResponse getMySubscription();

    void createSubscriptionRequest(
            SubscriptionRequestDto request
    );

    List<PendingSubscriptionRequestResponse>
    getPendingRequests();

    void approveRequest(UUID requestId);

    void rejectRequest(
            UUID requestId,
            RejectRequest request
    );

    List<SubscriptionRequestHistoryResponse> getRequestHistory();
}
