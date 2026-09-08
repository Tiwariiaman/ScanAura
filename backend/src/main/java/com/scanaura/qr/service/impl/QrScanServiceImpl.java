package com.scanaura.qr.service.impl;

import com.scanaura.business.entity.Business;
import com.scanaura.common.exception.BusinessException;
import com.scanaura.qr.entity.QrCode;
import com.scanaura.qr.repository.QrCodeRepository;
import com.scanaura.qr.repository.QrScanDailyRepository;
import com.scanaura.qr.service.QrScanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QrScanServiceImpl implements QrScanService {

    private final QrCodeRepository qrCodeRepository;
    private final QrScanDailyRepository qrScanDailyRepository;

    @Override
    @Transactional
    public void recordScan(String qrCode) {
        QrCode qrCodeEntity = qrCodeRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new BusinessException("QR Code not found."));

        if (!Boolean.TRUE.equals(qrCodeEntity.getActive())) {
            throw new BusinessException("QR Code is inactive.");
        }

        Business business = qrCodeEntity.getBusiness();

        if (business == null) {
            throw new BusinessException("QR Code is not assigned.");
        }

        LocalDate today = LocalDate.now();

        qrScanDailyRepository.incrementScan(
                UUID.randomUUID(),
                business.getId(),
                today
        );
    }
}