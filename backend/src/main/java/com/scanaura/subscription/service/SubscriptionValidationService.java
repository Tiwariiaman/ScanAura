package com.scanaura.subscription.service;

import com.scanaura.business.entity.Business;

public interface SubscriptionValidationService {

    void validateActiveSubscription();

    void validateAiImportLimit();

    void validateBusinessAccess(Business business);
}