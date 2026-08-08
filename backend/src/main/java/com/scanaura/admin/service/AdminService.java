package com.scanaura.admin.service;

import com.scanaura.admin.dto.BusinessSummaryResponse;
import com.scanaura.admin.dto.DashboardResponse;
import com.scanaura.admin.dto.QrInventoryResponse;

import java.util.List;
import java.util.UUID;

public interface AdminService {

    DashboardResponse getDashboard();

    List<BusinessSummaryResponse> getAllBusinesses();

    List<BusinessSummaryResponse> searchBusinesses(String keyword);

    void activateBusiness(UUID businessId);

    void deactivateBusiness(UUID businessId);

    QrInventoryResponse getQrInventory();

    void generatePhysicalQr(int count);

    void deactivateQr(String qrCode);

}
