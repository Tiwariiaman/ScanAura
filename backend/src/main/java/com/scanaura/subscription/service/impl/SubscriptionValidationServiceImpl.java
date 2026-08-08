package com.scanaura.subscription.service.impl;

import com.scanaura.auth.entity.User;
import com.scanaura.business.entity.Business;
import com.scanaura.business.repository.BusinessRepository;
import com.scanaura.common.enums.SubscriptionStatus;
import com.scanaura.common.exception.BusinessException;
import com.scanaura.common.util.SecurityUtil;
import com.scanaura.subscription.entity.Subscription;
import com.scanaura.subscription.repository.SubscriptionRepository;
import com.scanaura.subscription.service.SubscriptionValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionValidationServiceImpl
        implements SubscriptionValidationService {

    private final BusinessRepository businessRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    public void validateActiveSubscription() {

        User currentUser = SecurityUtil.getCurrentUser();

        Business business = businessRepository
                .findByOwner(currentUser)
                .orElseThrow(() ->
                        new BusinessException("Business not found."));

        Subscription subscription = subscriptionRepository
                .findByBusiness(business)
                .orElseThrow(() ->
                        new BusinessException("Subscription not found."));

        if (subscription.getStatus() != SubscriptionStatus.TRIAL &&
                subscription.getStatus() != SubscriptionStatus.ACTIVE) {

            throw new BusinessException(
                    "Your subscription has expired. Please renew your plan."
            );
        }

    }

    @Override
    public void validateAiImportLimit() {

        User currentUser = SecurityUtil.getCurrentUser();

        Business business = businessRepository
                .findByOwner(currentUser)
                .orElseThrow(() ->
                        new BusinessException("Business not found."));

        Subscription subscription = subscriptionRepository
                .findByBusiness(business)
                .orElseThrow(() ->
                        new BusinessException("Subscription not found."));

        Integer limit = subscription.getPlan().getAiImportLimit();

        if (limit != null &&
                subscription.getAiImportUsed() >= limit) {

            throw new BusinessException(
                    "AI import limit exceeded. Please upgrade your plan."
            );

        }

    }

}
