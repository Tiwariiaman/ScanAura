package com.scanaura.referral.repository;

import com.scanaura.referral.entity.ReferralPartner;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReferralPartnerRepository extends JpaRepository<ReferralPartner, UUID> {
    boolean existsByPin(String pin);
    Optional<ReferralPartner> findByPinAndActiveTrue(String pin);
    Optional<ReferralPartner> findByPin(String pin);
    List<ReferralPartner> findAllByOrderByCreatedAtDesc();
}
