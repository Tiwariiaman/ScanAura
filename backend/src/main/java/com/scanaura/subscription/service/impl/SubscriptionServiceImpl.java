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

import java.time.LocalDate;
import java.time.LocalDateTime;
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

        Plan basicPlan = planRepository.findByNameIgnoreCase("Basic")
                .orElseThrow(() ->
                        new BusinessException("Basic plan not found."));

        Subscription subscription = new Subscription();

        subscription.setBusiness(business);
        subscription.setPlan(basicPlan);

        subscription.setStatus(SubscriptionStatus.TRIAL);
        subscription.setBillingCycle(BillingCycle.MONTHLY);

        subscription.setStartDate(LocalDate.now());

        subscription.setEndDate(
                LocalDate.now()
                        .plusDays(basicPlan.getTrialDays())
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

//    @Override
//    public void requestUpgrade(UpgradeSubscriptionRequest request) {
//
//        User currentUser = SecurityUtil.getCurrentUser();
//
//        Business business = businessRepository
//                .findByOwner(currentUser)
//                .orElseThrow(() ->
//                        new BusinessException("Business not found."));
//
//        Subscription subscription = subscriptionRepository
//                .findByBusiness(business)
//                .orElseThrow(() ->
//                        new BusinessException("Subscription not found."));
//
//        Plan plan = planRepository
//                .findByNameIgnoreCase(request.getPlanName())
//                .orElseThrow(() ->
//                        new BusinessException("Plan not found."));
//
//        subscription.setPlan(plan);
//
//        subscription.setBillingCycle(request.getBillingCycle());
//
//        subscription.setTransactionId(request.getTransactionId());
//
//        subscription.setPaymentScreenshotUrl(
//                request.getPaymentScreenshotUrl()
//        );
//
//        subscription.setStatus(SubscriptionStatus.PENDING);
//
//        subscriptionRepository.save(subscription);
//    }

//    @Override
//    public List<PendingSubscriptionResponse> getPendingSubscriptions() {
//
//        return subscriptionRepository
//                .findByStatus(SubscriptionStatus.PENDING)
//                .stream()
//                .map(subscription ->
//
//                        PendingSubscriptionResponse.builder()
//                                .subscriptionId(subscription.getId())
//                                .businessId(subscription.getBusiness().getId())
//                                .businessName(subscription.getBusiness().getBusinessName())
//                                .planName(subscription.getPlan().getName())
//                                .billingCycle(subscription.getBillingCycle())
//                                .transactionId(subscription.getTransactionId())
//                                .paymentScreenshotUrl(subscription.getPaymentScreenshotUrl())
//                                .requestedDate(subscription.getCreatedAt().toLocalDate())
//                                .build()
//
//                )
//                .toList();
//    }

//    @Override
//    public void approveSubscription(UUID subscriptionId) {
//
//        Subscription subscription =
//                subscriptionRepository.findById(subscriptionId)
//                        .orElseThrow(() ->
//                                new BusinessException("Subscription not found."));
//
//        subscription.setStatus(SubscriptionStatus.ACTIVE);
//
//        subscription.setStartDate(LocalDate.now());
//
//        if (subscription.getBillingCycle() == BillingCycle.MONTHLY) {
//
//            subscription.setEndDate(
//                    LocalDate.now().plusMonths(1)
//            );
//
//        } else {
//
//            subscription.setEndDate(
//                    LocalDate.now().plusYears(1)
//            );
//
//        }
//
//        subscription.setApprovedAt(LocalDateTime.now());
//
//        subscription.setAiImportUsed(0);
//
//        subscriptionRepository.save(subscription);
//    }

//    @Override
//    public void rejectSubscription(
//            UUID subscriptionId,
//            String remark
//    ) {
//
//        Subscription subscription =
//                subscriptionRepository.findById(subscriptionId)
//                        .orElseThrow(() ->
//                                new BusinessException("Subscription not found."));
//
//        subscription.setStatus(SubscriptionStatus.REJECTED);
//
//        subscription.setAdminRemark(remark);
//
//        subscriptionRepository.save(subscription);
//    }

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

                ).toList();

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
    public List<SubscriptionRequestHistoryResponse> getRequestHistory() {

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
                                .planName(request.getPlan().getName())
                                .billingCycle(request.getBillingCycle())
                                .status(request.getStatus())
                                .transactionId(request.getTransactionId())
                                .paymentScreenshotUrl(request.getPaymentScreenshotUrl())
                                .adminRemark(request.getAdminRemark())
                                .requestedAt(request.getCreatedAt())
                                .build()

                )
                .toList();

    }

}