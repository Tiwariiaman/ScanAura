package com.scanaura.subscription.service.impl;

import com.scanaura.auth.entity.User;
import com.scanaura.business.entity.Business;
import com.scanaura.business.repository.BusinessRepository;
import com.scanaura.common.enums.BillingCycle;
import com.scanaura.common.enums.RequestStatus;
import com.scanaura.common.enums.SubscriptionStatus;
import com.scanaura.common.exception.BusinessException;
import com.scanaura.common.util.SecurityUtil;
import com.scanaura.subscription.dto.*;
import com.scanaura.subscription.entity.Plan;
import com.scanaura.subscription.entity.Subscription;
import com.scanaura.subscription.entity.SubscriptionRequest;
import com.scanaura.subscription.repository.PlanRepository;
import com.scanaura.subscription.repository.SubscriptionRepository;
import com.scanaura.subscription.repository.SubscriptionRequestRepository;
import com.scanaura.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final BusinessRepository businessRepository;
    private final SubscriptionRequestRepository subscriptionRequestRepository;

    @Override
    public void createTrialSubscription(Business business) {

        Plan trialPlan = planRepository
                .findByNameIgnoreCase("Trial")
                .orElseGet(() -> {

                    Plan plan = new Plan();

                    plan.setName("Trial");
                    plan.setMonthlyPrice(BigDecimal.ZERO);
                    plan.setYearlyPrice(BigDecimal.ZERO);
                    plan.setTrialDays(7);
                    plan.setAiImportLimit(3);
                    plan.setBrandedQr(false);
                    plan.setPrioritySupport(false);
                    plan.setActive(true);

                    return planRepository.save(plan);
                });

        Subscription subscription = new Subscription();

        subscription.setBusiness(business);
        subscription.setPlan(trialPlan);
        subscription.setStatus(SubscriptionStatus.TRIAL);
        subscription.setBillingCycle(BillingCycle.MONTHLY);

        subscription.setStartDate(LocalDate.now());

        subscription.setEndDate(
                LocalDate.now()
                        .plusDays(trialPlan.getTrialDays())
        );

        subscription.setAiImportUsed(0);

        business.setActive(true);
        businessRepository.save(business);

        subscriptionRepository.save(subscription);
    }

    @Override
    public SubscriptionResponse getMySubscription() {

        User currentUser = SecurityUtil.getCurrentUser();

        Business business = businessRepository
                .findByOwner(currentUser)
                .orElseThrow(() ->
                        new BusinessException("Business not found."));

        Subscription subscription = subscriptionRepository
                .findByBusiness(business)
                .orElseThrow(() ->
                        new BusinessException("Subscription not found."));

        expireIfNecessary(
                subscription,
                business
        );

        long daysLeft = 0;

        if (subscription.getEndDate() != null) {
            daysLeft = ChronoUnit.DAYS.between(
                    LocalDate.now(),
                    subscription.getEndDate()
            );

            if (daysLeft < 0) {
                daysLeft = 0;
            }
        }

        return mapToResponse(
                subscription,
                (int) daysLeft
        );
    }

    private void expireIfNecessary(
            Subscription subscription,
            Business business
    ) {

        if (subscription.getStatus() ==
                SubscriptionStatus.CANCELLED) {
            business.setActive(false);
            businessRepository.save(business);
            return;
        }

        if (subscription.getStatus() ==
                SubscriptionStatus.EXPIRED) {
            if (Boolean.TRUE.equals(business.getActive())) {
                business.setActive(false);
                businessRepository.save(business);
            }
            return;
        }

        LocalDate endDate = subscription.getEndDate();

        if (endDate != null &&
                LocalDate.now().isAfter(endDate)) {

            subscription.setStatus(
                    SubscriptionStatus.EXPIRED
            );

            subscriptionRepository.save(subscription);

            business.setActive(false);
            businessRepository.save(business);
        }
    }

    private SubscriptionResponse mapToResponse(
            Subscription subscription,
            Integer daysLeft
    ) {

        return SubscriptionResponse.builder()
                .planName(
                        subscription.getPlan().getName()
                )
                .status(
                        subscription.getStatus()
                )
                .billingCycle(
                        subscription.getBillingCycle()
                )
                .startDate(
                        subscription.getStartDate()
                )
                .endDate(
                        subscription.getEndDate()
                )
                .trialDaysLeft(
                        daysLeft
                )
                .aiImportLimit(
                        subscription.getPlan().getAiImportLimit()
                )
                .aiImportUsed(
                        subscription.getAiImportUsed()
                )
                .brandedQr(
                        subscription.getPlan().getBrandedQr()
                )
                .prioritySupport(
                        subscription.getPlan().getPrioritySupport()
                )
                .build();
    }

    @Override
    public void createSubscriptionRequest(
            SubscriptionRequestDto request
    ) {

        User currentUser = SecurityUtil.getCurrentUser();

        Business business = businessRepository
                .findByOwner(currentUser)
                .orElseThrow(() ->
                        new BusinessException("Business not found."));

        subscriptionRequestRepository
                .findByBusinessAndStatus(
                        business,
                        RequestStatus.PENDING
                )
                .ifPresent(req -> {
                    throw new BusinessException(
                            "You already have a pending payment request."
                    );
                });

        Plan plan = planRepository
                .findByNameIgnoreCase(request.getPlanName())
                .orElseThrow(() ->
                        new BusinessException("Plan not found."));

        SubscriptionRequest subscriptionRequest =
                new SubscriptionRequest();

        subscriptionRequest.setBusiness(business);
        subscriptionRequest.setPlan(plan);

        subscriptionRequest.setBillingCycle(
                request.getBillingCycle()
        );

        subscriptionRequest.setTransactionId(
                request.getTransactionId()
        );

        subscriptionRequest.setPaymentScreenshotUrl(
                request.getPaymentScreenshotUrl()
        );

        subscriptionRequest.setStatus(
                RequestStatus.PENDING
        );

        subscriptionRequestRepository.save(
                subscriptionRequest
        );
    }

    @Override
    public List<PendingSubscriptionRequestResponse>
    getPendingRequests() {

        return subscriptionRequestRepository
                .findByStatus(RequestStatus.PENDING)
                .stream()
                .map(request ->
                        PendingSubscriptionRequestResponse
                                .builder()
                                .requestId(request.getId())
                                .businessId(
                                        request.getBusiness().getId()
                                )
                                .businessName(
                                        request.getBusiness()
                                                .getBusinessName()
                                )
                                .planName(
                                        request.getPlan().getName()
                                )
                                .billingCycle(
                                        request.getBillingCycle()
                                )
                                .transactionId(
                                        request.getTransactionId()
                                )
                                .paymentScreenshotUrl(
                                        request.getPaymentScreenshotUrl()
                                )
                                .requestedAt(
                                        request.getCreatedAt()
                                )
                                .build()
                )
                .toList();
    }

    @Override
    public void approveRequest(UUID requestId) {

        SubscriptionRequest request =
                subscriptionRequestRepository
                        .findById(requestId)
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Request not found."
                                ));

        Business business = request.getBusiness();

        Subscription subscription =
                subscriptionRepository
                        .findByBusiness(business)
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Subscription not found."
                                ));

        subscription.setPlan(request.getPlan());

        subscription.setBillingCycle(
                request.getBillingCycle()
        );

        subscription.setStatus(
                SubscriptionStatus.ACTIVE
        );

        subscription.setAiImportUsed(0);

        subscription.setStartDate(
                LocalDate.now()
        );

        if (request.getBillingCycle() ==
                BillingCycle.MONTHLY) {

            subscription.setEndDate(
                    LocalDate.now().plusMonths(1)
            );

        } else {

            subscription.setEndDate(
                    LocalDate.now().plusYears(1)
            );
        }

        subscriptionRepository.save(subscription);

        /*
         * IMPORTANT:
         * Re-enable the business after successful renewal.
         * The QR code itself is never deleted or regenerated.
         */
        business.setActive(true);

        businessRepository.save(business);

        request.setStatus(
                RequestStatus.APPROVED
        );

        subscriptionRequestRepository.save(request);
    }

    @Override
    public void rejectRequest(
            UUID requestId,
            RejectRequest rejectRequest
    ) {

        SubscriptionRequest request =
                subscriptionRequestRepository
                        .findById(requestId)
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Request not found."
                                ));

        request.setStatus(
                RequestStatus.REJECTED
        );

        request.setAdminRemark(
                rejectRequest.getRemark()
        );

        subscriptionRequestRepository.save(
                request
        );
    }

    @Override
    public List<SubscriptionRequestHistoryResponse>
    getRequestHistory() {

        User currentUser = SecurityUtil.getCurrentUser();

        Business business = businessRepository
                .findByOwner(currentUser)
                .orElseThrow(() ->
                        new BusinessException("Business not found."));

        return subscriptionRequestRepository
                .findByBusiness(business)
                .stream()
                .map(request ->
                        SubscriptionRequestHistoryResponse
                                .builder()
                                .planName(
                                        request.getPlan().getName()
                                )
                                .billingCycle(
                                        request.getBillingCycle()
                                )
                                .status(
                                        request.getStatus()
                                )
                                .transactionId(
                                        request.getTransactionId()
                                )
                                .paymentScreenshotUrl(
                                        request.getPaymentScreenshotUrl()
                                )
                                .adminRemark(
                                        request.getAdminRemark()
                                )
                                .requestedAt(
                                        request.getCreatedAt()
                                )
                                .build()
                )
                .toList();
    }
}