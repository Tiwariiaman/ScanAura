package com.scanaura.qr.service.impl;

import com.scanaura.business.entity.Business;
import com.scanaura.business.repository.BusinessRepository;
import com.scanaura.common.enums.QrType;
import com.scanaura.common.exception.BusinessException;
import com.scanaura.common.util.QrCodeGenerator;
import com.scanaura.common.util.SecurityUtil;
import com.scanaura.qr.dto.AssignQrRequest;
import com.scanaura.qr.dto.DigitalQrResponse;
import com.scanaura.qr.dto.QrResponse;
import com.scanaura.qr.dto.QrStockResponse;
import com.scanaura.qr.entity.QrCode;
import com.scanaura.qr.repository.QrCodeRepository;
import com.scanaura.qr.service.QrService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QrServiceImpl implements QrService {

    private final QrCodeRepository qrCodeRepository;
    private final BusinessRepository businessRepository;

    @Override
    public DigitalQrResponse generateDigitalQr(UUID businessId) {

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() ->
                        new BusinessException("Business not found."));

        if (qrCodeRepository.findByBusinessAndType(business, QrType.DIGITAL).isPresent()) {
            throw new BusinessException("Digital QR already exists.");
        }

        QrCode qrCode = new QrCode();

        qrCode.setQrCode(
                QrCodeGenerator.generateDigitalQrCode()
        );

        qrCode.setBusiness(business);
        qrCode.setType(QrType.DIGITAL);
        qrCode.setAssigned(true);
        qrCode.setActive(true);

        qrCodeRepository.save(qrCode);

        return DigitalQrResponse.builder()
                .qrCode(qrCode.getQrCode())
                .menuUrl("/q/" + qrCode.getQrCode())
                .build();
    }

    private QrResponse mapToResponse(QrCode qrCode) {

        return QrResponse.builder()
                .id(qrCode.getId())
                .qrCode(qrCode.getQrCode())
                .type(qrCode.getType())
                .assigned(qrCode.getAssigned())
                .active(qrCode.getActive())
                .businessId(
                        qrCode.getBusiness() != null
                                ? qrCode.getBusiness().getId()
                                : null
                )
                .businessName(
                        qrCode.getBusiness() != null
                                ? qrCode.getBusiness().getBusinessName()
                                : null
                )
                .build();
    }

    @Override
    public List<QrResponse> assignPhysicalQrCodes(AssignQrRequest request) {

        Business business = businessRepository.findById(request.getBusinessId())
                .orElseThrow(() ->
                        new BusinessException("Business not found."));

        if (request.getQrCodes().size() != new HashSet<>(request.getQrCodes()).size()) {
            throw new BusinessException("Duplicate QR Codes in request.");
        }

        for (String qr : request.getQrCodes()) {

            QrCode qrCode = qrCodeRepository.findByQrCode(qr)
                    .orElseThrow(() ->
                            new BusinessException("QR Code not found : " + qr));

            if (Boolean.TRUE.equals(qrCode.getAssigned())) {
                throw new BusinessException("QR Code already assigned : " + qr);
            }

            qrCode.setBusiness(business);
            qrCode.setAssigned(true);
            qrCode.setType(QrType.PHYSICAL);
            qrCode.setActive(true);

            qrCodeRepository.save(qrCode);
        }

        return qrCodeRepository
                .findByBusinessOrderByTypeAscCreatedAtAsc(business)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<QrResponse> getMyQrCodes() {

        Business business = businessRepository
                .findByOwner(SecurityUtil.getCurrentUser())
                .orElseThrow(() ->
                        new BusinessException("Business not found."));

        return qrCodeRepository.findByBusiness(business)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Override
    public List<QrResponse> getAvailablePhysicalQrCodes() {

        return qrCodeRepository
                .findByAssignedFalseAndTypeAndActiveTrue(QrType.PHYSICAL)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public QrStockResponse getQrInventory() {

        long availablePhysical =
                qrCodeRepository.countByAssignedFalseAndTypeAndActiveTrue(QrType.PHYSICAL);

        long assignedPhysical =
                qrCodeRepository.countByAssignedTrueAndType(QrType.PHYSICAL);

        long digital =
                qrCodeRepository.countByType(QrType.DIGITAL);

        return QrStockResponse.builder()
                .availablePhysicalQr(availablePhysical)
                .assignedPhysicalQr(assignedPhysical)
                .digitalQr(digital)
                .totalQr(availablePhysical + assignedPhysical + digital)
                .build();
    }

    @Override
    public void deactivateQr(String qrCode) {

        QrCode qr = qrCodeRepository.findByQrCode(qrCode)
                .orElseThrow(() ->
                        new BusinessException("QR Code not found."));

        if (qr.getType() == QrType.DIGITAL) {
            throw new BusinessException("Digital QR cannot be deactivated.");
        }

        qr.setActive(false);

        qrCodeRepository.save(qr);
    }

    @Override
    public List<QrResponse> generatePhysicalQrCodes(int count) {

        long assigned =
                qrCodeRepository.countByAssignedTrueAndType(QrType.PHYSICAL);

        long available =
                qrCodeRepository.countByAssignedFalseAndTypeAndActiveTrue(QrType.PHYSICAL);

        long sequence = assigned + available + 1;

        List<QrCode> generatedQrCodes = new ArrayList<>();

        for (int i = 0; i < count; i++) {

            String qrCodeValue =
                    QrCodeGenerator.generatePhysicalQrCode(sequence++);

            while (qrCodeRepository.existsByQrCode(qrCodeValue)) {

                qrCodeValue =
                        QrCodeGenerator.generatePhysicalQrCode(sequence++);
            }

            QrCode qrCode = new QrCode();

            qrCode.setQrCode(qrCodeValue);
            qrCode.setType(QrType.PHYSICAL);
            qrCode.setAssigned(false);
            qrCode.setActive(true);

            generatedQrCodes.add(
                    qrCodeRepository.save(qrCode)
            );
        }

        return generatedQrCodes
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public QrResponse getMyDigitalQr() {

        Business business = businessRepository
                .findByOwner(SecurityUtil.getCurrentUser())
                .orElseThrow(() ->
                        new BusinessException("Business not found."));

        QrCode qrCode = qrCodeRepository
                .findByBusinessAndType(business, QrType.DIGITAL)
                .orElseThrow(() ->
                        new BusinessException("Digital QR not found."));

        return mapToResponse(qrCode);
    }

}