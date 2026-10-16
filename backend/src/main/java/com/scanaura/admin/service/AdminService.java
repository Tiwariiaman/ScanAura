package com.scanaura.admin.service;

import com.scanaura.admin.dto.*;
import com.scanaura.qr.dto.QrResponse;

import java.util.List;
import java.util.UUID;

public interface AdminService {

    DashboardResponse getDashboard();

    List<BusinessSummaryResponse> getAllBusinesses();

    List<BusinessSummaryResponse> searchBusinesses(String keyword);

    void activateBusiness(UUID businessId);

    void deactivateBusiness(UUID businessId);

    QrInventoryResponse getQrInventory();

    List<QrResponse> generatePhysicalQr(int count);

    void deactivateQr(String qrCode);

    AdminQrDetailsResponse getQrDetails(String qrCode);

    AdminImpersonationResponse loginAsBusiness(
            UUID businessId,
            String adminIdentifier
    );


}
