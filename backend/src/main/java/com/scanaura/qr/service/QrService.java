package com.scanaura.qr.service;

import com.scanaura.publicapi.dto.PublicQrResponse;
import com.scanaura.qr.dto.AssignQrRequest;
import com.scanaura.qr.dto.DigitalQrResponse;
import com.scanaura.qr.dto.QrResponse;
import com.scanaura.qr.dto.QrStockResponse;

import java.util.List;
import java.util.UUID;

public interface QrService {

    // Auto generate one DIGITAL QR after business creation
    DigitalQrResponse generateDigitalQr(UUID businessId);

    // Assign one or more PHYSICAL QR Codes
    List<QrResponse> assignPhysicalQrCodes(AssignQrRequest request);

    // Get all QR codes of logged-in business
    List<QrResponse> getMyQrCodes();

    // Admin - Available physical QR inventory
    List<QrResponse> getAvailablePhysicalQrCodes();

    // Admin - Inventory Dashboard
    QrStockResponse getQrInventory();

    // Admin - Deactivate QR
    void deactivateQr(String qrCode);

    List<QrResponse> generatePhysicalQrCodes(int count);

    QrResponse getMyDigitalQr();

    PublicQrResponse resolvePublicQr(String qrCode);


}