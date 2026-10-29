package com.scanaura.activity.scratch.service;

import com.scanaura.activity.scratch.dto.*;
import com.scanaura.activity.scratch.entity.ScratchCard;
import com.scanaura.activity.scratch.entity.ScratchPlay;
import com.scanaura.activity.scratch.entity.ScratchReward;
import com.scanaura.activity.scratch.repository.ScratchCardRepository;
import com.scanaura.activity.scratch.repository.ScratchPlayRepository;
import com.scanaura.activity.scratch.repository.ScratchRewardRepository;
import com.scanaura.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ScratchService {

    private static final ZoneId INDIA_ZONE = ZoneId.of("Asia/Kolkata");

    private final ScratchCardRepository cardRepository;
    private final ScratchRewardRepository rewardRepository;
    private final ScratchPlayRepository playRepository;

    @Transactional
    public ScratchSettingsResponse getSettings(UUID businessId) {
        ScratchCard card = cardRepository.findByBusinessId(businessId)
                .orElseGet(() -> createDefaultCard(businessId));
        return toSettingsResponse(card);
    }

    @Transactional
    public ScratchSettingsResponse updateSettings(UUID businessId, ScratchSettingsRequest request) {
        if (Boolean.TRUE.equals(request.getEnabled())
                && (request.getRewards() == null || request.getRewards().isEmpty())) {
            throw new BusinessException("Add at least one reward before enabling Scratch Card.");
        }

        ScratchCard card = cardRepository.findByBusinessId(businessId)
                .orElseGet(() -> {
                    ScratchCard created = new ScratchCard();
                    created.setBusinessId(businessId);
                    return created;
                });
        card.setCardName(request.getName().trim());
        card.setEnabled(Boolean.TRUE.equals(request.getEnabled()));
        card = cardRepository.save(card);

        // Reward configuration can be replaced. Existing plays retain their reward snapshot.
        rewardRepository.deleteAllByCardId(card.getId());
        List<ScratchRewardInput> inputs = request.getRewards() == null
                ? List.of()
                : request.getRewards();
        for (int i = 0; i < inputs.size(); i++) {
            ScratchRewardInput input = inputs.get(i);
            if (input.getName() == null || input.getName().isBlank()) {
                throw new BusinessException("Reward names cannot be empty.");
            }
            ScratchReward reward = new ScratchReward();
            reward.setCardId(card.getId());
            reward.setBusinessId(businessId);
            reward.setRewardName(input.getName().trim());
            reward.setDisplayOrder(i);
            rewardRepository.save(reward);
        }
        return toSettingsResponse(card);
    }

    @Transactional(readOnly = true)
    public ScratchPublicStatusResponse getPublicStatus(UUID businessId) {
        return cardRepository.findByBusinessId(businessId)
                .map(card -> new ScratchPublicStatusResponse(card.getCardName(), card.isEnabled()))
                .orElseGet(() -> new ScratchPublicStatusResponse("Scratch & Win", false));
    }

    @Transactional
    public ScratchPlayResponse play(UUID businessId, ScratchPlayRequest request) {
        // Lock this business' card row first. Requests for another business use a different lock,
        // so the same phone may play at multiple businesses on the same IST calendar day.
        ScratchCard card = cardRepository.findByBusinessIdForUpdate(businessId)
                .orElseThrow(() -> new BusinessException("Scratch Card is not configured for this business."));
        if (!card.isEnabled()) {
            throw new BusinessException("Scratch Card is currently unavailable.");
        }

        List<ScratchReward> rewards = rewardRepository.findAllByCardIdOrderByDisplayOrderAsc(card.getId());
        if (rewards.isEmpty()) {
            throw new BusinessException("Scratch Card has no rewards configured.");
        }

        String normalizedMobile = normalizeMobile(request.getMobileNumber());

        // Fixed IST calendar windows, not a rolling 24-hour cooldown: reset at midnight India time.
        LocalDate todayInIndia = LocalDate.now(INDIA_ZONE);
        LocalDateTime dayStart = todayInIndia.atStartOfDay();
        LocalDateTime nextDayStart = todayInIndia.plusDays(1).atStartOfDay();
        boolean alreadyPlayedToday = playRepository
                .existsByBusinessIdAndMobileNumberAndPlayedAtGreaterThanEqualAndPlayedAtLessThan(
                        businessId,
                        normalizedMobile,
                        dayStart,
                        nextDayStart
                );
        if (alreadyPlayedToday) {
            throw new BusinessException(
                    "This mobile number has already played at this business today. Try again after midnight IST."
            );
        }

        // Every configured reward has an equal chance. No probability settings are exposed.
        ScratchReward selected = rewards.get(ThreadLocalRandom.current().nextInt(rewards.size()));
        ScratchPlay play = new ScratchPlay();
        play.setCardId(card.getId());
        play.setBusinessId(businessId);
        play.setRewardId(selected.getId());
        play.setCustomerName(request.getCustomerName().trim());
        play.setMobileNumber(normalizedMobile);
        play.setRewardNameSnapshot(selected.getRewardName());
        play.setCouponToken(UUID.randomUUID());
        play.setCouponCode("SA-" + UUID.randomUUID().toString().replace("-", "")
                .substring(0, 10).toUpperCase(Locale.ROOT));
        play.setStatus(ScratchPlay.PlayStatus.WON);
        play.setPlayedAt(LocalDateTime.now(INDIA_ZONE));
        play = playRepository.save(play);
        return toPlayResponse(play);
    }

    @Transactional
    public ScratchPlayReportResponse redeem(UUID businessId, UUID playId) {
        // A coupon is redeemable only by the business that issued it.
        ScratchPlay play = playRepository.findPlayForUpdate(playId, businessId)
                .orElseThrow(() -> new BusinessException("Scratch Card entry not found for this business."));
        if (play.getStatus() == ScratchPlay.PlayStatus.REDEEMED) {
            throw new BusinessException("This coupon has already been redeemed.");
        }
        play.setStatus(ScratchPlay.PlayStatus.REDEEMED);
        play.setRedeemedAt(LocalDateTime.now(INDIA_ZONE));
        play = playRepository.save(play);
        return toReportResponse(play);
    }

    @Transactional(readOnly = true)
    public List<ScratchPlayReportResponse> getRecentPlays(UUID businessId) {
        return playRepository.findTop100ByBusinessIdOrderByPlayedAtDesc(businessId)
                .stream().map(this::toReportResponse).toList();
    }

    private String normalizeMobile(String rawMobile) {
        String digits = rawMobile == null ? "" : rawMobile.replaceAll("\\D", "");
        // Treat common Indian formatting variants as the same mobile number.
        if (digits.length() == 12 && digits.startsWith("91")) {
            digits = digits.substring(2);
        } else if (digits.length() == 13 && digits.startsWith("910")) {
            digits = digits.substring(3);
        } else if (digits.length() == 11 && digits.startsWith("0")) {
            digits = digits.substring(1);
        } else if (digits.length() == 14 && digits.startsWith("0091")) {
            digits = digits.substring(4);
        }
        if (digits.length() < 7 || digits.length() > 15) {
            throw new BusinessException("Enter a valid mobile number.");
        }
        return digits;
    }

    private ScratchCard createDefaultCard(UUID businessId) {
        ScratchCard card = new ScratchCard();
        card.setBusinessId(businessId);
        card.setCardName("Scratch & Win");
        card.setEnabled(false);
        return cardRepository.save(card);
    }

    private ScratchSettingsResponse toSettingsResponse(ScratchCard card) {
        List<String> rewards = rewardRepository.findAllByCardIdOrderByDisplayOrderAsc(card.getId())
                .stream().map(ScratchReward::getRewardName).toList();
        return new ScratchSettingsResponse(card.getId(), card.getBusinessId(), card.getCardName(),
                card.isEnabled(), rewards);
    }

    private ScratchPlayResponse toPlayResponse(ScratchPlay play) {
        return new ScratchPlayResponse(play.getId(), play.getRewardNameSnapshot(), play.getCouponCode(),
                play.getCouponToken(), play.getStatus(), play.getPlayedAt());
    }

    private ScratchPlayReportResponse toReportResponse(ScratchPlay play) {
        return new ScratchPlayReportResponse(play.getId(), play.getCustomerName(), play.getMobileNumber(),
                play.getRewardNameSnapshot(), play.getCouponCode(), play.getStatus(), play.getPlayedAt(),
                play.getRedeemedAt());
    }
}
