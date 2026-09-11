package com.scanaura.activity.loyalty.service;

import com.scanaura.activity.loyalty.dto.LoyaltyClaimRequest;
import com.scanaura.activity.loyalty.dto.LoyaltyClaimResponse;
import com.scanaura.activity.loyalty.dto.LoyaltyClaimVerifyRequest;
import com.scanaura.activity.loyalty.dto.LoyaltyCustomerRequest;
import com.scanaura.activity.loyalty.dto.LoyaltyCustomerResponse;
import com.scanaura.activity.loyalty.dto.LoyaltyRewardRequest;
import com.scanaura.activity.loyalty.dto.LoyaltyRewardResponse;
import com.scanaura.activity.loyalty.dto.LoyaltyRewardUpdateRequest;
import com.scanaura.activity.loyalty.dto.LoyaltySettingsRequest;
import com.scanaura.activity.loyalty.dto.LoyaltySettingsResponse;
import com.scanaura.activity.loyalty.dto.LoyaltyTransactionResponse;
import com.scanaura.activity.loyalty.dto.LoyaltyVisitQrResponse;
import com.scanaura.activity.loyalty.dto.LoyaltyVisitRequest;
import com.scanaura.activity.loyalty.entity.LoyaltyClaim;
import com.scanaura.activity.loyalty.entity.LoyaltyCustomer;
import com.scanaura.activity.loyalty.entity.LoyaltyReward;
import com.scanaura.activity.loyalty.entity.LoyaltySettings;
import com.scanaura.activity.loyalty.entity.LoyaltyTransaction;
import com.scanaura.activity.loyalty.entity.LoyaltyVisitQr;
import com.scanaura.activity.loyalty.mapper.LoyaltyMapper;
import com.scanaura.activity.loyalty.repository.LoyaltyClaimRepository;
import com.scanaura.activity.loyalty.repository.LoyaltyCustomerRepository;
import com.scanaura.activity.loyalty.repository.LoyaltyRewardRepository;
import com.scanaura.activity.loyalty.repository.LoyaltySettingsRepository;
import com.scanaura.activity.loyalty.repository.LoyaltyTransactionRepository;
import com.scanaura.activity.loyalty.repository.LoyaltyVisitQrRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoyaltyService {

    private static final int DAILY_POINTS = 10;
    private static final int VISIT_QR_VALIDITY_MINUTES = 5;

    private static final String VISIT_QR_TYPE =
            "SCANAURA_LOYALTY_VISIT";

    private static final String VISIT_QR_VERSION =
            "1";

    private final LoyaltySettingsRepository loyaltySettingsRepository;
    private final LoyaltyRewardRepository loyaltyRewardRepository;
    private final LoyaltyCustomerRepository loyaltyCustomerRepository;
    private final LoyaltyTransactionRepository loyaltyTransactionRepository;
    private final LoyaltyClaimRepository loyaltyClaimRepository;
    private final LoyaltyVisitQrRepository loyaltyVisitQrRepository;
    private final LoyaltyMapper loyaltyMapper;
    private final LoyaltyQrService loyaltyQrService;


    // ============================================================
    // LOYALTY SETTINGS
    // ============================================================

    @Transactional(readOnly = true)
    public LoyaltySettingsResponse getSettings(UUID businessId) {

        LoyaltySettings settings =
                loyaltySettingsRepository
                        .findByBusinessId(businessId)
                        .orElseGet(() ->
                                LoyaltySettings.builder()
                                        .businessId(businessId)
                                        .enabled(false)
                                        .pointsPerVisit(DAILY_POINTS)
                                        .build()
                        );

        return loyaltyMapper.toSettingsResponse(settings);
    }


    @Transactional
    public LoyaltySettingsResponse updateSettings(
            UUID businessId,
            LoyaltySettingsRequest request
    ) {

        LoyaltySettings settings =
                loyaltySettingsRepository
                        .findByBusinessId(businessId)
                        .orElseGet(() ->
                                LoyaltySettings.builder()
                                        .businessId(businessId)
                                        .pointsPerVisit(DAILY_POINTS)
                                        .build()
                        );

        settings.setEnabled(request.getEnabled());
        settings.setPointsPerVisit(DAILY_POINTS);

        return loyaltyMapper.toSettingsResponse(
                loyaltySettingsRepository.save(settings)
        );
    }


    // ============================================================
    // CUSTOMER REGISTRATION / UPSERT
    // ============================================================

    @Transactional
    public LoyaltyCustomerResponse createOrUpdateCustomer(
            UUID businessId,
            LoyaltyCustomerRequest request
    ) {

        LoyaltySettings settings =
                loyaltySettingsRepository
                        .findByBusinessId(businessId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Loyalty program is not configured."
                                )
                        );

        if (!Boolean.TRUE.equals(settings.getEnabled())) {
            throw new IllegalStateException(
                    "Loyalty program is currently unavailable."
            );
        }

        String mobileNumber =
                request.getMobileNumber().trim();

        String customerName =
                request.getCustomerName().trim();

        if (mobileNumber.isEmpty()) {
            throw new IllegalArgumentException(
                    "Mobile number is required."
            );
        }

        if (customerName.isEmpty()) {
            throw new IllegalArgumentException(
                    "Customer name is required."
            );
        }

        LoyaltyCustomer customer =
                loyaltyCustomerRepository
                        .findByBusinessIdAndMobileNumber(
                                businessId,
                                mobileNumber
                        )
                        .orElseGet(() ->
                                LoyaltyCustomer.builder()
                                        .businessId(businessId)
                                        .mobileNumber(mobileNumber)
                                        .customerName(customerName)
                                        .pointsBalance(0)
                                        .totalPointsEarned(0)
                                        .totalPointsRedeemed(0)
                                        .build()
                        );

        customer.setCustomerName(customerName);

        return loyaltyMapper.toCustomerResponse(
                loyaltyCustomerRepository.save(customer)
        );
    }


    // ============================================================
    // CUSTOMER
    // ============================================================

    @Transactional(readOnly = true)
    public LoyaltyCustomerResponse getCustomer(
            UUID businessId,
            String mobileNumber
    ) {

        LoyaltyCustomer customer =
                loyaltyCustomerRepository
                        .findByBusinessIdAndMobileNumber(
                                businessId,
                                mobileNumber.trim()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Loyalty customer not found."
                                )
                        );

        return loyaltyMapper.toCustomerResponse(customer);
    }


    // ============================================================
    // GENERATE TEMPORARY VISIT QR
    // ============================================================

    @Transactional
    public LoyaltyVisitQrResponse createVisitQr(
            UUID businessId,
            String mobileNumber
    ) {

        LoyaltySettings settings =
                loyaltySettingsRepository
                        .findByBusinessId(businessId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Loyalty program is not configured."
                                )
                        );

        if (!Boolean.TRUE.equals(settings.getEnabled())) {
            throw new IllegalStateException(
                    "Loyalty program is currently unavailable."
            );
        }

        LoyaltyCustomer customer =
                loyaltyCustomerRepository
                        .findByBusinessIdAndMobileNumber(
                                businessId,
                                mobileNumber.trim()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Loyalty customer not found."
                                )
                        );

        if (hasEarnedToday(
                businessId,
                customer.getId()
        )) {
            throw new IllegalStateException(
                    "You have already earned loyalty points today."
            );
        }

        LocalDateTime now =
                LocalDateTime.now(ZoneId.of("Asia/Kolkata"));

        LocalDateTime expiresAt =
                now.plusMinutes(
                        VISIT_QR_VALIDITY_MINUTES
                );

        UUID qrToken =
                UUID.randomUUID();

        LoyaltyVisitQr visitQr =
                LoyaltyVisitQr.builder()
                        .businessId(businessId)
                        .customerId(customer.getId())
                        .qrToken(qrToken)
                        .expiresAt(expiresAt)
                        .build();

        loyaltyVisitQrRepository.save(visitQr);

        return LoyaltyVisitQrResponse.create(
                qrToken,
                expiresAt
        );
    }


    // ============================================================
    // VERIFY TEMPORARY VISIT QR
    // ============================================================

    @Transactional
    public LoyaltyCustomerResponse verifyCustomerVisitQr(
            UUID businessId,
            UUID qrToken
    ) {

        LoyaltyVisitQr visitQr =
                loyaltyVisitQrRepository
                        .findByQrTokenAndBusinessId(
                                qrToken,
                                businessId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid loyalty QR."
                                )
                        );

        if (visitQr.isUsed()) {
            throw new IllegalStateException(
                    "This loyalty QR has already been used."
            );
        }

        if (visitQr.isExpired()) {
            throw new IllegalStateException(
                    "This loyalty QR has expired."
            );
        }

        LoyaltyCustomer customer =
                loyaltyCustomerRepository
                        .findById(visitQr.getCustomerId())
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Loyalty customer not found."
                                )
                        );

        if (hasEarnedToday(
                businessId,
                customer.getId()
        )) {
            throw new IllegalStateException(
                    "You have already earned loyalty points today."
            );
        }

        LoyaltySettings settings =
                loyaltySettingsRepository
                        .findByBusinessId(businessId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Loyalty program is not configured."
                                )
                        );

        if (!Boolean.TRUE.equals(settings.getEnabled())) {
            throw new IllegalStateException(
                    "Loyalty program is currently unavailable."
            );
        }

        int points =
                settings.getPointsPerVisit() != null
                        ? settings.getPointsPerVisit()
                        : DAILY_POINTS;

        customer.setPointsBalance(
                customer.getPointsBalance() + points
        );

        customer.setTotalPointsEarned(
                customer.getTotalPointsEarned() + points
        );

        LoyaltyCustomer savedCustomer =
                loyaltyCustomerRepository.save(customer);

        LoyaltyTransaction transaction =
                LoyaltyTransaction.builder()
                        .businessId(businessId)
                        .customerId(savedCustomer.getId())
                        .transactionType(
                                LoyaltyTransaction.TransactionType.EARN
                        )
                        .points(points)
                        .description("Daily visit reward")
                        .build();

        loyaltyTransactionRepository.save(transaction);

        visitQr.setUsedAt(LocalDateTime.now());

        loyaltyVisitQrRepository.save(visitQr);

        LoyaltyCustomerResponse response =
                loyaltyMapper.toCustomerResponse(
                        savedCustomer
                );

        response.setPointsAwarded(points);

        return response;
    }


    // ============================================================
    // CHECK DAILY EARNING
    // ============================================================

    private boolean hasEarnedToday(
            UUID businessId,
            UUID customerId
    ) {

        LocalDate today =
                LocalDate.now(
                        ZoneId.of("Asia/Kolkata")
                );

        LocalDateTime startOfDay =
                today.atStartOfDay();

        LocalDateTime endOfDay =
                LocalDateTime.of(
                        today,
                        LocalTime.MAX
                );

        return loyaltyTransactionRepository
                .findFirstByBusinessIdAndCustomerIdAndTransactionTypeAndCreatedAtBetween(
                        businessId,
                        customerId,
                        LoyaltyTransaction.TransactionType.EARN,
                        startOfDay,
                        endOfDay
                )
                .isPresent();
    }


    // ============================================================
    // LEGACY DIRECT VISIT METHOD
    // ============================================================

    @Transactional
    public LoyaltyCustomerResponse recordVisit(
            UUID businessId,
            LoyaltyVisitRequest request
    ) {

        LoyaltySettings settings =
                loyaltySettingsRepository
                        .findByBusinessId(businessId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Loyalty program is not configured."
                                )
                        );

        if (!Boolean.TRUE.equals(settings.getEnabled())) {
            throw new IllegalStateException(
                    "Loyalty program is currently unavailable."
            );
        }

        String mobileNumber =
                request.getMobileNumber().trim();

        String customerName =
                request.getCustomerName().trim();

        LoyaltyCustomer customer =
                loyaltyCustomerRepository
                        .findByBusinessIdAndMobileNumber(
                                businessId,
                                mobileNumber
                        )
                        .orElseGet(() ->
                                LoyaltyCustomer.builder()
                                        .businessId(businessId)
                                        .mobileNumber(mobileNumber)
                                        .customerName(customerName)
                                        .pointsBalance(0)
                                        .totalPointsEarned(0)
                                        .totalPointsRedeemed(0)
                                        .build()
                        );

        customer.setCustomerName(customerName);

        if (hasEarnedToday(
                businessId,
                customer.getId()
        )) {
            throw new IllegalStateException(
                    "You have already earned loyalty points today."
            );
        }

        customer.setPointsBalance(
                customer.getPointsBalance() + DAILY_POINTS
        );

        customer.setTotalPointsEarned(
                customer.getTotalPointsEarned() + DAILY_POINTS
        );

        LoyaltyCustomer savedCustomer =
                loyaltyCustomerRepository.save(customer);

        LoyaltyTransaction transaction =
                LoyaltyTransaction.builder()
                        .businessId(businessId)
                        .customerId(savedCustomer.getId())
                        .transactionType(
                                LoyaltyTransaction.TransactionType.EARN
                        )
                        .points(DAILY_POINTS)
                        .description("Daily visit reward")
                        .build();

        loyaltyTransactionRepository.save(transaction);

        return loyaltyMapper.toCustomerResponse(
                savedCustomer
        );
    }


    // ============================================================
    // REWARDS
    // ============================================================

    @Transactional(readOnly = true)
    public List<LoyaltyRewardResponse> getRewards(
            UUID businessId
    ) {

        return loyaltyRewardRepository
                .findAllByBusinessIdOrderByPointsRequiredAsc(
                        businessId
                )
                .stream()
                .map(loyaltyMapper::toRewardResponse)
                .toList();
    }


    @Transactional(readOnly = true)
    public List<LoyaltyRewardResponse> getActiveRewards(
            UUID businessId
    ) {

        return loyaltyRewardRepository
                .findAllByBusinessIdAndActiveTrueOrderByPointsRequiredAsc(
                        businessId
                )
                .stream()
                .map(loyaltyMapper::toRewardResponse)
                .toList();
    }


    @Transactional
    public LoyaltyRewardResponse createReward(
            UUID businessId,
            LoyaltyRewardRequest request
    ) {

        LoyaltyReward reward =
                LoyaltyReward.builder()
                        .businessId(businessId)
                        .title(request.getTitle().trim())
                        .pointsRequired(request.getPointsRequired())
                        .rewardAmount(request.getRewardAmount())
                        .active(true)
                        .build();

        return loyaltyMapper.toRewardResponse(
                loyaltyRewardRepository.save(reward)
        );
    }


    @Transactional
    public LoyaltyRewardResponse updateReward(
            UUID businessId,
            UUID rewardId,
            LoyaltyRewardUpdateRequest request
    ) {

        LoyaltyReward reward =
                loyaltyRewardRepository
                        .findByIdAndBusinessId(
                                rewardId,
                                businessId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Reward not found."
                                )
                        );

        reward.setTitle(request.getTitle().trim());
        reward.setPointsRequired(
                request.getPointsRequired()
        );
        reward.setRewardAmount(
                request.getRewardAmount()
        );
        reward.setActive(
                request.getActive()
        );

        return loyaltyMapper.toRewardResponse(
                loyaltyRewardRepository.save(reward)
        );
    }


    @Transactional
    public void deleteReward(
            UUID businessId,
            UUID rewardId
    ) {

        LoyaltyReward reward =
                loyaltyRewardRepository
                        .findByIdAndBusinessId(
                                rewardId,
                                businessId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Reward not found."
                                )
                        );

        loyaltyRewardRepository.delete(reward);
    }


    // ============================================================
    // CLAIM REWARD
    // ============================================================

    @Transactional
    public LoyaltyClaimResponse claimReward(
            UUID businessId,
            String mobileNumber,
            LoyaltyClaimRequest request
    ) {

        LoyaltyCustomer customer =
                loyaltyCustomerRepository
                        .findByBusinessIdAndMobileNumber(
                                businessId,
                                mobileNumber.trim()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Loyalty customer not found."
                                )
                        );

        LoyaltyReward reward =
                loyaltyRewardRepository
                        .findByIdAndBusinessId(
                                request.getRewardId(),
                                businessId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Reward not found."
                                )
                        );

        if (!Boolean.TRUE.equals(reward.getActive())) {
            throw new IllegalStateException(
                    "This reward is currently unavailable."
            );
        }

        if (customer.getPointsBalance()
                < reward.getPointsRequired()) {

            throw new IllegalStateException(
                    "You do not have enough loyalty points."
            );
        }

        customer.setPointsBalance(
                customer.getPointsBalance()
                        - reward.getPointsRequired()
        );

        customer.setTotalPointsRedeemed(
                customer.getTotalPointsRedeemed()
                        + reward.getPointsRequired()
        );

        loyaltyCustomerRepository.save(customer);

        LoyaltyTransaction transaction =
                LoyaltyTransaction.builder()
                        .businessId(businessId)
                        .customerId(customer.getId())
                        .transactionType(
                                LoyaltyTransaction.TransactionType.REDEEM
                        )
                        .points(reward.getPointsRequired())
                        .description(
                                "Reward claimed: "
                                        + reward.getTitle()
                        )
                        .build();

        loyaltyTransactionRepository.save(transaction);

        LoyaltyClaim claim =
                LoyaltyClaim.builder()
                        .businessId(businessId)
                        .customerId(customer.getId())
                        .rewardId(reward.getId())
                        .pointsUsed(
                                reward.getPointsRequired()
                        )
                        .rewardAmount(
                                reward.getRewardAmount()
                        )
                        .qrToken(UUID.randomUUID())
                        .status(
                                LoyaltyClaim.ClaimStatus.PENDING
                        )
                        .build();

        LoyaltyClaim savedClaim =
                loyaltyClaimRepository.save(claim);

        return loyaltyMapper.toClaimResponse(
                savedClaim,
                reward,
                customer.getPointsBalance()
        );
    }


    // ============================================================
    // VERIFY / GRANT REWARD
    // ============================================================

    @Transactional
    public LoyaltyClaimResponse verifyAndGrantReward(
            UUID businessId,
            LoyaltyClaimVerifyRequest request
    ) {

        LoyaltyClaim claim =
                loyaltyClaimRepository
                        .findByQrTokenAndBusinessId(
                                request.getQrToken(),
                                businessId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid reward QR."
                                )
                        );

        if (claim.getStatus()
                != LoyaltyClaim.ClaimStatus.PENDING) {

            throw new IllegalStateException(
                    "This reward has already been processed."
            );
        }

        LoyaltyReward reward =
                loyaltyRewardRepository
                        .findByIdAndBusinessId(
                                claim.getRewardId(),
                                businessId
                        )
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Reward no longer exists."
                                )
                        );

        claim.setStatus(
                LoyaltyClaim.ClaimStatus.GRANTED
        );

        claim.setGrantedAt(
                LocalDateTime.now()
        );

        LoyaltyClaim savedClaim =
                loyaltyClaimRepository.save(claim);

        LoyaltyCustomer customer =
                loyaltyCustomerRepository
                        .findById(claim.getCustomerId())
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Loyalty customer not found."
                                )
                        );

        return loyaltyMapper.toClaimResponse(
                savedClaim,
                reward,
                customer.getPointsBalance()
        );
    }


    // ============================================================
    // TRANSACTION HISTORY
    // ============================================================

    @Transactional(readOnly = true)
    public List<LoyaltyTransactionResponse> getTransactions(
            UUID businessId,
            UUID customerId
    ) {

        return loyaltyTransactionRepository
                .findAllByBusinessIdAndCustomerIdOrderByCreatedAtDesc(
                        businessId,
                        customerId
                )
                .stream()
                .map(loyaltyMapper::toTransactionResponse)
                .toList();
    }


    // ============================================================
    // LEGACY CLAIM QR VERIFICATION
    // ============================================================

    @Transactional
    public LoyaltyCustomerResponse verifyCustomerVisit(
            UUID businessId,
            UUID qrToken
    ) {

        LoyaltyClaim claim =
                loyaltyClaimRepository
                        .findByQrTokenAndBusinessId(
                                qrToken,
                                businessId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid or expired loyalty QR."
                                )
                        );

        if (claim.getStatus()
                != LoyaltyClaim.ClaimStatus.PENDING) {

            throw new IllegalStateException(
                    "This loyalty QR has already been used."
            );
        }

        LoyaltyCustomer customer =
                loyaltyCustomerRepository
                        .findById(claim.getCustomerId())
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Loyalty customer not found."
                                )
                        );

        LoyaltyVisitRequest visitRequest =
                new LoyaltyVisitRequest(
                        customer.getMobileNumber(),
                        customer.getCustomerName()
                );

        LoyaltyCustomerResponse response =
                recordVisit(
                        businessId,
                        visitRequest
                );

        claim.setStatus(
                LoyaltyClaim.ClaimStatus.GRANTED
        );

        claim.setGrantedAt(
                LocalDateTime.now()
        );

        loyaltyClaimRepository.save(claim);

        return response;
    }


    // ============================================================
    // REWARD CLAIM QR PAYLOAD
    // ============================================================

    @Transactional(readOnly = true)
    public String createClaimQrPayload(
            UUID businessId,
            UUID claimId
    ) {

        LoyaltyClaim claim =
                loyaltyClaimRepository
                        .findByIdAndBusinessId(
                                claimId,
                                businessId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Loyalty claim not found."
                                )
                        );

        if (claim.getStatus()
                != LoyaltyClaim.ClaimStatus.PENDING) {

            throw new IllegalStateException(
                    "This loyalty claim is no longer active."
            );
        }

        return loyaltyQrService.createQrPayload(
                claim.getQrToken()
        );
    }
}