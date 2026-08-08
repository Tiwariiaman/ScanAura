package com.scanaura.subscription.scheduler;

import com.scanaura.common.enums.SubscriptionStatus;
import com.scanaura.subscription.entity.Subscription;
import com.scanaura.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionScheduler {

    private final SubscriptionRepository subscriptionRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void expireSubscriptions() {

        List<Subscription> subscriptions = subscriptionRepository.findAll();

        for (Subscription subscription : subscriptions) {

            if (subscription.getStatus() == SubscriptionStatus.CANCELLED) {
                continue;
            }

            if (subscription.getEndDate() == null) {
                continue;
            }

            if (subscription.getEndDate().isBefore(LocalDate.now())) {

                subscription.setStatus(SubscriptionStatus.EXPIRED);

                subscriptionRepository.save(subscription);

                log.info(
                        "Subscription expired for business: {}",
                        subscription.getBusiness().getBusinessName()
                );

            }

        }

    }

}
