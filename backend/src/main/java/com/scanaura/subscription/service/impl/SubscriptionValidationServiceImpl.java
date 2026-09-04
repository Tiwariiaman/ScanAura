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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SubscriptionValidationServiceImpl
        implements SubscriptionValidationService {

    private final BusinessRepository businessRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    @Transactional
    public void validateActiveSubscription() {

        User currentUser = SecurityUtil.getCurrentUser();

        Business business = businessRepository
                .findByOwner(currentUser)
                .orElseThrow(() ->
                        new BusinessException(
                                "Business not found."
                        ));

        validateBusinessAccess(business);
    }

    @Override
    @Transactional
    public void validateAiImportLimit() {

        User currentUser = SecurityUtil.getCurrentUser();

        Business business = businessRepository
                .findByOwner(currentUser)
                .orElseThrow(() ->
                        new BusinessException(
                                "Business not found."
                        ));

        Subscription subscription = findSubscription(business);

        validateBusinessAccess(business);

        Integer limit = subscription.getPlan().getAiImportLimit();

        if (limit != null &&
                limit >= 0 &&
                subscription.getAiImportUsed() >= limit) {

            throw new BusinessException(
                    "AI import limit exceeded. Please upgrade your plan."
            );
        }
    }

    @Override
    @Transactional
    public void validateBusinessAccess(Business business) {

        if (business == null) {
            throw new BusinessException(
                    "Business not found."
            );
        }

        Subscription subscription = findSubscription(business);

        /*
         * Explicitly cancelled subscriptions are not publicly accessible.
         */
        if (subscription.getStatus() ==
                SubscriptionStatus.CANCELLED) {

            deactivateBusiness(business);

            throw new BusinessException(
                    "This business is currently unavailable. Please try again later."
            );
        }

        /*
         * Missing end date is an invalid subscription state.
         * Block access rather than accidentally exposing the business.
         */
        if (subscription.getEndDate() == null) {

            deactivateBusiness(business);

            throw new BusinessException(
                    "This business is currently unavailable. Please try again later."
            );
        }

        /*
         * The end date is the final valid date.
         *
         * Example:
         * endDate = September 10
         *
         * September 10 -> valid
         * September 11 -> expired
         */
        if (LocalDate.now().isAfter(
                subscription.getEndDate()
        )) {

            subscription.setStatus(
                    SubscriptionStatus.EXPIRED
            );

            subscriptionRepository.save(subscription);

            deactivateBusiness(business);

            throw new BusinessException(
                    "This business is currently unavailable. Please try again later."
            );
        }

        /*
         * Already marked EXPIRED by scheduler or another request.
         */
        if (subscription.getStatus() ==
                SubscriptionStatus.EXPIRED) {

            deactivateBusiness(business);

            throw new BusinessException(
                    "This business is currently unavailable. Please try again later."
            );
        }

        /*
         * Only TRIAL and ACTIVE subscriptions can serve
         * public content.
         */
        if (subscription.getStatus() !=
                SubscriptionStatus.TRIAL &&
                subscription.getStatus() !=
                        SubscriptionStatus.ACTIVE) {

            deactivateBusiness(business);

            throw new BusinessException(
                    "This business is currently unavailable. Please try again later."
            );
        }

        /*
         * Subscription is valid.
         *
         * A valid subscription should make the business publicly active.
         * This is important after a renewal.
         */
        if (!Boolean.TRUE.equals(business.getActive())) {

            business.setActive(true);
            businessRepository.save(business);
        }
    }

    private Subscription findSubscription(
            Business business
    ) {

        return subscriptionRepository
                .findByBusiness(business)
                .orElseThrow(() ->
                        new BusinessException(
                                "Subscription not found."
                        ));
    }

    private void deactivateBusiness(
            Business business
    ) {

        if (Boolean.TRUE.equals(business.getActive())) {

            business.setActive(false);
            businessRepository.save(business);
        }
    }
}