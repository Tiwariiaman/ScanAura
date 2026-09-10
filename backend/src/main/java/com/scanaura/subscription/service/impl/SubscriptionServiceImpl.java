package com.scanaura.subscription.service.impl;

import com.scanaura.auth.entity.User;
import com.scanaura.business.entity.Business;
import com.scanaura.business.repository.BusinessRepository;
import com.scanaura.common.enums.BillingCycle;
import com.scanaura.common.enums.RequestStatus;
import com.scanaura.common.enums.SubscriptionStatus;
import com.scanaura.common.exception.BusinessException;
import com.scanaura.common.util.SecurityUtil;
import com.scanaura.image.service.ImageService;
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
import org.springframework.transaction.annotation.Transactional;

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
    private final ImageService imageService;

    @Override
    public void createTrialSubscription(Business business) {

        Plan trialPlan = planRepository
                .findByNameIgnoreCase("Trial")
                .orElseGet(() -> {

                    Plan plan = new Plan();

                    plan.setName("Trial");
                    plan.setMonthlyPrice(BigDecimal.ZERO);
                    plan.setHalfYearlyPrice(BigDecimal.ZERO);
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

        long daysLeft = ChronoUnit.DAYS.between(
                LocalDate.now(),
                subscription.getEndDate()
        );

        if (daysLeft < 0) {
            daysLeft = 0;
        }

        return mapToResponse(subscription, (int) daysLeft);
    }

    private SubscriptionResponse mapToResponse(
            Subscription subscription,
            Integer daysLeft
    ) {

        BigDecimal price = null;

        if (subscription.getBillingCycle() == BillingCycle.MONTHLY) {

            price = subscription.getPlan().getMonthlyPrice();

        } else if (subscription.getBillingCycle() == BillingCycle.HALF_YEARLY) {

            price = subscription.getPlan().getHalfYearlyPrice();

        } else if (subscription.getBillingCycle() == BillingCycle.YEARLY) {

            price = subscription.getPlan().getYearlyPrice();
        }

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
                .price(price)
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

        if (!Boolean.TRUE.equals(plan.getActive())) {
            throw new BusinessException("Selected plan is inactive.");
        }

        if (request.getBillingCycle() == BillingCycle.MONTHLY
                && plan.getMonthlyPrice() == null) {

            throw new BusinessException(
                    "Monthly pricing is not available for this plan."
            );
        }

        if (request.getBillingCycle() == BillingCycle.HALF_YEARLY
                && plan.getHalfYearlyPrice() == null) {

            throw new BusinessException(
                    "Half-yearly pricing is not available for this plan."
            );
        }

        if (request.getBillingCycle() == BillingCycle.YEARLY
                && plan.getYearlyPrice() == null) {

            throw new BusinessException(
                    "Yearly pricing is not available for this plan."
            );
        }

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

        subscriptionRequest.setPaymentScreenshotPublicId(
                request.getPaymentScreenshotPublicId()
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

                ).toList();
    }

    @Override
    @Transactional
    public void approveRequest(UUID requestId) {

        SubscriptionRequest request =
                subscriptionRequestRepository
                        .findById(requestId)
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Request not found."
                                ));

        Subscription subscription =
                subscriptionRepository
                        .findByBusiness(
                                request.getBusiness()
                        )
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

        if (request.getBillingCycle() == BillingCycle.MONTHLY) {

            subscription.setEndDate(
                    LocalDate.now().plusMonths(1)
            );

        } else if (request.getBillingCycle() == BillingCycle.HALF_YEARLY) {

            subscription.setEndDate(
                    LocalDate.now().plusMonths(6)
            );

        } else if (request.getBillingCycle() == BillingCycle.YEARLY) {

            subscription.setEndDate(
                    LocalDate.now().plusYears(1)
            );

        } else {

            throw new BusinessException(
                    "Unsupported billing cycle."
            );
        }

        subscriptionRepository.save(subscription);

        request.setStatus(
                RequestStatus.APPROVED
        );

        subscriptionRequestRepository.save(request);

        String paymentScreenshotPublicId =
                request.getPaymentScreenshotPublicId();

        if (paymentScreenshotPublicId != null
                && !paymentScreenshotPublicId.trim().isEmpty()) {

            imageService.delete(
                    paymentScreenshotPublicId.trim()
            );

            request.setPaymentScreenshotPublicId(null);

            subscriptionRequestRepository.save(request);
        }
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

    @Override
    @Transactional
    public void grantSubscription(
            UUID businessId,
            String planName,
            BillingCycle billingCycle
    ) {

        Business business = businessRepository
                .findById(businessId)
                .orElseThrow(() ->
                        new BusinessException("Business not found."));

        Plan plan = planRepository
                .findByNameIgnoreCase(planName)
                .orElseThrow(() ->
                        new BusinessException("Plan not found."));

        if (!Boolean.TRUE.equals(plan.getActive())) {
            throw new BusinessException("Selected plan is inactive.");
        }

        Subscription subscription = subscriptionRepository
                .findByBusiness(business)
                .orElseThrow(() ->
                        new BusinessException("Subscription not found."));

        LocalDate today = LocalDate.now();

        subscription.setPlan(plan);
        subscription.setBillingCycle(billingCycle);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartDate(today);
        subscription.setAiImportUsed(0);

        if (billingCycle == BillingCycle.MONTHLY) {

            subscription.setEndDate(
                    today.plusMonths(1)
            );

        } else if (billingCycle == BillingCycle.HALF_YEARLY) {

            subscription.setEndDate(
                    today.plusMonths(6)
            );

        } else if (billingCycle == BillingCycle.YEARLY) {

            subscription.setEndDate(
                    today.plusYears(1)
            );

        } else {

            throw new BusinessException(
                    "Unsupported billing cycle."
            );
        }

        subscriptionRepository.save(subscription);
    }
}