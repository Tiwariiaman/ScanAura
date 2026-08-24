package com.scanaura.admin.service.impl;

import com.scanaura.admin.dto.AdminQrDetailsResponse;
import com.scanaura.admin.dto.BusinessSummaryResponse;
import com.scanaura.admin.dto.DashboardResponse;
import com.scanaura.admin.dto.QrInventoryResponse;
import com.scanaura.admin.service.AdminService;
import com.scanaura.business.entity.Business;
import com.scanaura.business.repository.BusinessRepository;
import com.scanaura.common.enums.QrType;
import com.scanaura.common.enums.RequestStatus;
import com.scanaura.common.enums.SubscriptionStatus;
import com.scanaura.common.exception.BusinessException;
import com.scanaura.qr.dto.QrResponse;
import com.scanaura.qr.dto.QrStockResponse;
import com.scanaura.qr.entity.QrCode;
import com.scanaura.qr.repository.QrCodeRepository;
import com.scanaura.qr.service.QrService;
import com.scanaura.subscription.entity.Subscription;
import com.scanaura.subscription.repository.SubscriptionRepository;
import com.scanaura.subscription.repository.SubscriptionRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final BusinessRepository businessRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionRequestRepository subscriptionRequestRepository;
    private final QrCodeRepository qrCodeRepository;
    private final QrService qrService;



    @Override
    public DashboardResponse getDashboard() {

        return DashboardResponse.builder()

                .totalBusinesses(
                        businessRepository.count()
                )

                .activeBusinesses(
                        businessRepository.countByActiveTrue()
                )

                .inactiveBusinesses(
                        businessRepository.countByActiveFalse()
                )

                .trialSubscriptions(
                        subscriptionRepository.countByStatus(
                                SubscriptionStatus.TRIAL
                        )
                )

                .activeSubscriptions(
                        subscriptionRepository.countByStatus(
                                SubscriptionStatus.ACTIVE
                        )
                )

                .expiredSubscriptions(
                        subscriptionRepository.countByStatus(
                                SubscriptionStatus.EXPIRED
                        )
                )

                .pendingSubscriptionRequests(
                        subscriptionRequestRepository.countByStatus(
                                RequestStatus.PENDING
                        )
                )

                .availablePhysicalQr(
                        qrCodeRepository.countByAssignedFalseAndTypeAndActiveTrue(
                                QrType.PHYSICAL
                        )
                )

                .assignedPhysicalQr(
                        qrCodeRepository.countByAssignedTrueAndType(
                                QrType.PHYSICAL
                        )
                )

                .digitalQrGenerated(
                        qrCodeRepository.countByType(
                                QrType.DIGITAL
                        )
                )

                .build();

    }

    @Override
    public List<BusinessSummaryResponse> getAllBusinesses() {

        return businessRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapBusiness)
                .toList();
    }

    @Override
    public List<BusinessSummaryResponse> searchBusinesses(
            String keyword
    ) {

        return businessRepository
                .findByBusinessNameContainingIgnoreCase(keyword)
                .stream()
                .map(this::mapBusiness)
                .toList();
    }

    @Override
    public void activateBusiness(UUID businessId) {

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() ->
                        new BusinessException("Business not found."));

        business.setActive(true);

        businessRepository.save(business);
    }

    @Override
    public void deactivateBusiness(UUID businessId) {

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() ->
                        new BusinessException("Business not found."));

        business.setActive(false);

        businessRepository.save(business);
    }

    private BusinessSummaryResponse mapBusiness(
            Business business
    ) {

        Subscription subscription =
                subscriptionRepository
                        .findByBusiness(business)
                        .orElse(null);

        return BusinessSummaryResponse.builder()

                .businessId(
                        business.getId()
                )

                .businessName(
                        business.getBusinessName()
                )

                .ownerName(
                        business.getOwner().getFullName()
                )

                .email(
                        business.getEmail()
                )

                .phone(
                        business.getPhone()
                )

                .city(
                        business.getCity()
                )

                .active(
                        business.getActive()
                )

                .subscriptionStatus(
                        subscription != null
                                ? subscription.getStatus()
                                : null
                )

                .currentPlan(
                        subscription != null
                                ? subscription.getPlan().getName()
                                : null
                )

                .build();
    }


    @Override
    public QrInventoryResponse getQrInventory() {

        QrStockResponse stock =
                qrService.getQrInventory();

        return QrInventoryResponse.builder()

                .availablePhysicalQr(
                        stock.getAvailablePhysicalQr()
                )

                .assignedPhysicalQr(
                        stock.getAssignedPhysicalQr()
                )

                .digitalQr(
                        stock.getDigitalQr()
                )

                .totalQr(
                        stock.getTotalQr()
                )

                .build();

    }

    @Override
    public List<QrResponse> generatePhysicalQr(
            int count
    ) {

        return qrService.generatePhysicalQrCodes(
                count
        );
    }

    @Override
    public void deactivateQr(String qrCode) {

        qrService.deactivateQr(qrCode);

    }

    @Override
    public AdminQrDetailsResponse getQrDetails(
            String qrCode
    ) {

        QrCode qrCodeEntity =
                qrCodeRepository.findByQrCode(qrCode)
                        .orElseThrow(() ->
                                new BusinessException(
                                        "QR code not found."
                                ));

        return AdminQrDetailsResponse.builder()
                .id(qrCodeEntity.getId())
                .qrCode(qrCodeEntity.getQrCode())
                .type(qrCodeEntity.getType())
                .active(qrCodeEntity.getActive())
                .assigned(qrCodeEntity.getAssigned())
                .businessId(
                        qrCodeEntity.getBusiness() != null
                                ? qrCodeEntity.getBusiness().getId()
                                : null
                )
                .businessName(
                        qrCodeEntity.getBusiness() != null
                                ? qrCodeEntity.getBusiness()
                                .getBusinessName()
                                : null
                )
                .build();
    }

}