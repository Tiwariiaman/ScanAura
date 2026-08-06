package com.scanaura.business.service.impl;

import com.scanaura.auth.entity.User;
import com.scanaura.business.dto.BusinessRequest;
import com.scanaura.business.dto.BusinessResponse;
import com.scanaura.business.entity.Business;
import com.scanaura.business.repository.BusinessRepository;
import com.scanaura.business.service.BusinessService;
import com.scanaura.common.exception.BusinessException;
import com.scanaura.common.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BusinessServiceImpl implements BusinessService {

    private final BusinessRepository businessRepository;

    @Override
    public BusinessResponse createBusiness(BusinessRequest request) {

        User currentUser = SecurityUtil.getCurrentUser();

        if (businessRepository.existsByOwner(currentUser)) {
            throw new BusinessException("Business already exists.");
        }

        Business business = new Business();

        business.setOwner(currentUser);
        business.setBusinessName(request.getBusinessName());
        business.setBusinessType(request.getBusinessType());
        business.setPhone(request.getPhone());
        business.setWhatsapp(request.getWhatsapp());
        business.setEmail(request.getEmail());
        business.setAddress(request.getAddress());
        business.setCity(request.getCity());
        business.setState(request.getState());
        business.setCountry(request.getCountry());
        business.setPincode(request.getPincode());
        business.setWebsite(request.getWebsite());
        business.setDescription(request.getDescription());
        business.setUpiId(request.getUpiId());

        business.setQrSlug(UUID.randomUUID().toString());

        business.setActive(true);

        Business savedBusiness = businessRepository.save(business);

        return mapToResponse(savedBusiness);
    }

    @Override
    public BusinessResponse getMyBusiness() {

        User currentUser = SecurityUtil.getCurrentUser();

        Business business = businessRepository.findByOwner(currentUser)
                .orElseThrow(() ->
                        new BusinessException("Business not found."));

        return mapToResponse(business);
    }

    @Override
    public BusinessResponse updateBusiness(BusinessRequest request) {

        User currentUser = SecurityUtil.getCurrentUser();

        Business business = businessRepository.findByOwner(currentUser)
                .orElseThrow(() ->
                        new BusinessException("Business not found."));

        business.setBusinessName(request.getBusinessName());
        business.setBusinessType(request.getBusinessType());
        business.setPhone(request.getPhone());
        business.setWhatsapp(request.getWhatsapp());
        business.setEmail(request.getEmail());
        business.setAddress(request.getAddress());
        business.setCity(request.getCity());
        business.setState(request.getState());
        business.setCountry(request.getCountry());
        business.setPincode(request.getPincode());
        business.setWebsite(request.getWebsite());
        business.setDescription(request.getDescription());
        business.setUpiId(request.getUpiId());

        Business updatedBusiness = businessRepository.save(business);

        return mapToResponse(updatedBusiness);
    }

    @Override
    public void deleteBusiness() {

        User currentUser = SecurityUtil.getCurrentUser();

        Business business = businessRepository.findByOwner(currentUser)
                .orElseThrow(() ->
                        new BusinessException("Business not found."));

        businessRepository.delete(business);
    }

    private BusinessResponse mapToResponse(Business business) {

        return BusinessResponse.builder()
                .id(business.getId())
                .businessName(business.getBusinessName())
                .businessType(business.getBusinessType())
                .logoUrl(business.getLogoUrl())
                .phone(business.getPhone())
                .whatsapp(business.getWhatsapp())
                .email(business.getEmail())
                .address(business.getAddress())
                .city(business.getCity())
                .state(business.getState())
                .country(business.getCountry())
                .pincode(business.getPincode())
                .website(business.getWebsite())
                .description(business.getDescription())
                .upiId(business.getUpiId())
                .qrSlug(business.getQrSlug())
                .active(business.getActive())
                .build();
    }
}
