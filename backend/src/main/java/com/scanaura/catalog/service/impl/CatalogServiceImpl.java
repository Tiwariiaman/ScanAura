package com.scanaura.catalog.service.impl;

import com.scanaura.auth.entity.User;
import com.scanaura.business.entity.Business;
import com.scanaura.business.repository.BusinessRepository;
import com.scanaura.catalog.dto.CatalogRequest;
import com.scanaura.catalog.dto.CatalogResponse;
import com.scanaura.catalog.entity.Catalog;
import com.scanaura.catalog.repository.CatalogRepository;
import com.scanaura.catalog.service.CatalogService;
import com.scanaura.category.entity.Category;
import com.scanaura.category.repository.CategoryRepository;
import com.scanaura.common.exception.BusinessException;
import com.scanaura.common.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CatalogServiceImpl implements CatalogService {

    private final CatalogRepository catalogRepository;
    private final BusinessRepository businessRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public CatalogResponse createCatalog(CatalogRequest request) {

        User currentUser = SecurityUtil.getCurrentUser();

        Business business = businessRepository.findByOwner(currentUser)
                .orElseThrow(() -> new BusinessException("Business not found."));

        Category category = null;

        if (request.getCategoryId() != null) {
            category = categoryRepository.findByIdAndBusiness(
                            request.getCategoryId(),
                            business)
                    .orElseThrow(() -> new BusinessException("Category not found."));
        }

        Catalog catalog = new Catalog();

        catalog.setBusiness(business);
        catalog.setCategory(category);

        catalog.setName(request.getName());
        catalog.setDescription(request.getDescription());
        catalog.setPrice(request.getPrice());
        catalog.setImageUrl(request.getImageUrl());

        catalog.setVeg(request.getVeg());
        catalog.setAvailable(request.getAvailable());
        catalog.setBestSeller(request.getBestSeller());
        catalog.setRecommended(request.getRecommended());

        catalog.setDisplayOrder(request.getDisplayOrder());
        catalog.setActive(true);

        Catalog saved = catalogRepository.save(catalog);

        return mapToResponse(saved);
    }

    @Override
    public List<CatalogResponse> getCatalogs(UUID categoryId) {

        User currentUser = SecurityUtil.getCurrentUser();

        Business business = businessRepository.findByOwner(currentUser)
                .orElseThrow(() -> new BusinessException("Business not found."));

        return catalogRepository.findByBusinessOrderByDisplayOrderAsc(business)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CatalogResponse getCatalog(UUID catalogId) {

        User currentUser = SecurityUtil.getCurrentUser();

        Business business = businessRepository.findByOwner(currentUser)
                .orElseThrow(() -> new BusinessException("Business not found."));

        Catalog catalog = catalogRepository.findByIdAndBusiness(
                        catalogId,
                        business)
                .orElseThrow(() -> new BusinessException("Catalog not found."));

        return mapToResponse(catalog);
    }

    @Override
    public CatalogResponse updateCatalog(UUID catalogId,
                                         CatalogRequest request) {

        User currentUser = SecurityUtil.getCurrentUser();

        Business business = businessRepository.findByOwner(currentUser)
                .orElseThrow(() -> new BusinessException("Business not found."));

        Catalog catalog = catalogRepository.findByIdAndBusiness(
                        catalogId,
                        business)
                .orElseThrow(() -> new BusinessException("Catalog not found."));

        Category category = null;

        if (request.getCategoryId() != null) {
            category = categoryRepository.findByIdAndBusiness(
                            request.getCategoryId(),
                            business)
                    .orElseThrow(() -> new BusinessException("Category not found."));
        }

        catalog.setCategory(category);

        catalog.setName(request.getName());
        catalog.setDescription(request.getDescription());
        catalog.setPrice(request.getPrice());
        catalog.setImageUrl(request.getImageUrl());

        catalog.setVeg(request.getVeg());
        catalog.setAvailable(request.getAvailable());
        catalog.setBestSeller(request.getBestSeller());
        catalog.setRecommended(request.getRecommended());

        catalog.setDisplayOrder(request.getDisplayOrder());

        Catalog updated = catalogRepository.save(catalog);

        return mapToResponse(updated);
    }

    @Override
    public void deleteCatalog(UUID catalogId) {

        User currentUser = SecurityUtil.getCurrentUser();

        Business business = businessRepository.findByOwner(currentUser)
                .orElseThrow(() -> new BusinessException("Business not found."));

        Catalog catalog = catalogRepository.findByIdAndBusiness(
                        catalogId,
                        business)
                .orElseThrow(() -> new BusinessException("Catalog not found."));

        catalogRepository.delete(catalog);
    }

    private CatalogResponse mapToResponse(Catalog catalog) {

        return CatalogResponse.builder()
                .id(catalog.getId())
                .categoryId(
                        catalog.getCategory() != null
                                ? catalog.getCategory().getId()
                                : null
                )
                .categoryName(
                        catalog.getCategory() != null
                                ? catalog.getCategory().getName()
                                : null
                )
                .name(catalog.getName())
                .description(catalog.getDescription())
                .price(catalog.getPrice())
                .imageUrl(catalog.getImageUrl())
                .veg(catalog.getVeg())
                .available(catalog.getAvailable())
                .bestSeller(catalog.getBestSeller())
                .recommended(catalog.getRecommended())
                .displayOrder(catalog.getDisplayOrder())
                .active(catalog.getActive())
                .build();
    }
}
