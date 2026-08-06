package com.scanaura.business.service;

import com.scanaura.business.dto.BusinessRequest;
import com.scanaura.business.dto.BusinessResponse;

public interface BusinessService {

    BusinessResponse createBusiness(BusinessRequest request);

    BusinessResponse getMyBusiness();

    BusinessResponse updateBusiness(BusinessRequest request);

    void deleteBusiness();

}
