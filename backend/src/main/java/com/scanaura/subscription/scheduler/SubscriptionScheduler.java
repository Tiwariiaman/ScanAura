package com.scanaura.subscription.scheduler;

import com.scanaura.business.entity.Business;
import com.scanaura.business.repository.BusinessRepository;
import com.scanaura.common.enums.SubscriptionStatus;
import com.scanaura.subscription.entity.Subscription;
import com.scanaura.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionScheduler {

    private final SubscriptionRepository subscriptionRepository;
    private final BusinessRepository businessRepository;

    /**
     * Runs every hour.
     *
     * Real-time validation also happens when protected APIs are called.
     * This scheduler keeps database state synchronized in the background.
     */
    @Transactional
    @Scheduled(cron = "0 0 * * * *")
    public void expireSubscriptions() {

        LocalDate today = LocalDate.now();

        List<Subscription> subscriptions =
                subscriptionRepository.findAll();

        int expiredCount = 0;

        for (Subscription subscription : subscriptions) {

            if (subscription.getStatus() ==
                    SubscriptionStatus.CANCELLED) {
                continue;
            }

            if (subscription.getStatus() ==
                    SubscriptionStatus.EXPIRED) {
                continue;
            }

            if (subscription.getEndDate() == null) {
                continue;
            }

            /*
             * End date is the final valid day.
             */
            if (today.isAfter(
                    subscription.getEndDate()
            )) {

                subscription.setStatus(
                        SubscriptionStatus.EXPIRED
                );

                Business business =
                        subscription.getBusiness();

                if (business != null) {

                    business.setActive(false);
                    businessRepository.save(business);
                }

                subscriptionRepository.save(subscription);

                expiredCount++;

                log.info(
                        "Subscription expired and business deactivated: {}",
                        business != null
                                ? business.getBusinessName()
                                : "unknown"
                );
            }
        }

        if (expiredCount > 0) {

            log.info(
                    "Subscription expiry job completed. {} subscription(s) expired.",
                    expiredCount
            );
        }
    }
}