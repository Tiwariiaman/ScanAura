package com.scanaura.referral.service;

import com.scanaura.business.repository.BusinessRepository;
import com.scanaura.referral.dto.CreateReferralPartnerRequest;
import com.scanaura.referral.dto.ReferralPartnerResponse;
import com.scanaura.referral.entity.ReferralPartner;
import com.scanaura.referral.repository.ReferralPartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReferralPartnerService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final ReferralPartnerRepository partnerRepository;
    private final BusinessRepository businessRepository;

    @Transactional
    public ReferralPartnerResponse create(CreateReferralPartnerRequest request) {
        for (int attempt = 0; attempt < 100; attempt++) {
            String pin = "SA" + String.format("%04d", RANDOM.nextInt(10_000));
            if ("SA1203".equals(pin) || partnerRepository.existsByPin(pin)) continue;
            ReferralPartner partner = new ReferralPartner();
            partner.setName(request.getName().trim());
            partner.setPhone(request.getPhone().trim());
            partner.setPin(pin);
            partner.setActive(true);
            try {
                return ReferralPartnerResponse.from(partnerRepository.saveAndFlush(partner), 0);
            } catch (DataIntegrityViolationException collision) {
                // Concurrent PIN generation collision: retry with a new random value.
            }
        }
        throw new IllegalStateException("Could not generate a unique referral PIN. Please retry.");
    }

    @Transactional(readOnly = true)
    public List<ReferralPartnerResponse> list() {
        return partnerRepository.findAllByOrderByCreatedAtDesc().stream()
            .map(p -> ReferralPartnerResponse.from(p, businessRepository.countByOwner_ReferralPartner_Id(p.getId())))
            .toList();
    }

    @Transactional
    public ReferralPartnerResponse setActive(UUID id, boolean active) {
        ReferralPartner partner = partnerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Referral partner not found"));
        partner.setActive(active);
        return ReferralPartnerResponse.from(partnerRepository.save(partner), businessRepository.countByOwner_ReferralPartner_Id(id));
    }
}
