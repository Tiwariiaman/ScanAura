package com.scanaura.publicapi.service;

import com.scanaura.publicapi.dto.LandingResponse;
import com.scanaura.publicapi.dto.MenuResponse;
import com.scanaura.publicapi.dto.PaymentResponse;
import com.scanaura.publicapi.dto.SharePreviewResponse;
import com.scanaura.business.entity.Business;

import java.util.List;

public interface PublicService {

    LandingResponse getLandingPage(String qrCode);

    MenuResponse getMenu(String qrCode);

    PaymentResponse getPaymentDetails(String qrCode);

    SharePreviewResponse getSharePreview(String qrCode);

    SharePreviewResponse getPublicBusinessPage(String publicSlug);

    List<Business> getBusinessesForSitemap();

}