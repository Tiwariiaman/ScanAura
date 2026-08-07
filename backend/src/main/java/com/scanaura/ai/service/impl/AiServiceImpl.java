package com.scanaura.ai.service.impl;

import com.scanaura.ai.client.AiClient;
import com.scanaura.ai.dto.AiImportRequest;
import com.scanaura.ai.dto.AiMenuResponse;
import com.scanaura.ai.service.AiService;
import com.scanaura.business.entity.Business;
import com.scanaura.business.repository.BusinessRepository;
import com.scanaura.catalog.entity.Catalog;
import com.scanaura.catalog.repository.CatalogRepository;
import com.scanaura.category.entity.Category;
import com.scanaura.category.repository.CategoryRepository;
import com.scanaura.common.exception.BusinessException;
import com.scanaura.common.util.SecurityUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Transactional
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final AiClient aiClient;

    private final BusinessRepository businessRepository;

    private final CategoryRepository categoryRepository;

    private final CatalogRepository catalogRepository;

    @Override
    public AiMenuResponse analyzeMenu(MultipartFile file) {

        if (file.isEmpty()) {
            throw new BusinessException("Menu file is required.");
        }

        String contentType = file.getContentType();

        if (contentType == null ||
                !(contentType.equals("image/jpeg")
                        || contentType.equals("image/png")
                        || contentType.equals("image/webp")
                        || contentType.equals("application/pdf"))) {

            throw new BusinessException(
                    "Only JPG, PNG, WEBP and PDF files are supported."
            );
        }

        return aiClient.analyzeMenu(file);

    }

    @Override
    public void importMenu(AiImportRequest request) {

        Business business = businessRepository
                .findByOwner(SecurityUtil.getCurrentUser())
                .orElseThrow(() ->
                        new BusinessException("Business not found."));

        if (Boolean.TRUE.equals(request.getOverwriteExistingMenu())) {

            catalogRepository.deleteByBusiness(business);

            categoryRepository.deleteByBusiness(business);

        }

        int categoryOrder = 1;

        int catalogOrder = 1;

        for (var aiCategory : request.getCategories()) {

            Category category = null;

            if (aiCategory.getCategoryName() != null &&
                    !aiCategory.getCategoryName().isBlank()) {

                category = categoryRepository
                        .findByBusinessAndNameIgnoreCase(
                                business,
                                aiCategory.getCategoryName()
                        )
                        .orElse(null);

                if (category == null) {

                    category = new Category();

                    category.setBusiness(business);
                    category.setName(aiCategory.getCategoryName());
                    category.setDisplayOrder(categoryOrder++);

                    category = categoryRepository.save(category);
                }
            }

            for (var item : aiCategory.getItems()) {



                if (item.getName() == null || item.getName().isBlank()) {
                    continue;
                }

                if (item.getPrice() == null) {
                    continue;
                }

                if (catalogRepository.existsByBusinessAndNameIgnoreCase(
                        business,
                        item.getName())) {

                    continue;

                }

                Catalog catalog = new Catalog();

                catalog.setBusiness(business);
                catalog.setCategory(category);

                catalog.setName(item.getName());
                catalog.setDescription(item.getDescription());

                catalog.setPrice(item.getPrice());

                catalog.setVeg(item.getVeg());

                catalog.setAvailable(true);

                catalog.setBestSeller(false);

                catalog.setRecommended(false);

                catalog.setDisplayOrder(catalogOrder++);

                catalogRepository.save(catalog);

            }

        }

    }

}
