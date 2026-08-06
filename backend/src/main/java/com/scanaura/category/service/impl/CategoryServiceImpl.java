package com.scanaura.category.service.impl;

import com.scanaura.auth.entity.User;
import com.scanaura.business.entity.Business;
import com.scanaura.business.repository.BusinessRepository;
import com.scanaura.category.dto.CategoryRequest;
import com.scanaura.category.dto.CategoryResponse;
import com.scanaura.category.entity.Category;
import com.scanaura.category.repository.CategoryRepository;
import com.scanaura.category.service.CategoryService;
import com.scanaura.common.exception.BusinessException;
import com.scanaura.common.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final BusinessRepository businessRepository;

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {

        User currentUser = SecurityUtil.getCurrentUser();

        Business business = businessRepository.findByOwner(currentUser)
                .orElseThrow(() -> new BusinessException("Business not found."));

        if (categoryRepository.existsByBusinessAndName(business, request.getName())) {
            throw new BusinessException("Category already exists.");
        }

        Category category = new Category();

        category.setBusiness(business);
        category.setName(request.getName());
        category.setDisplayOrder(request.getDisplayOrder());
        category.setActive(true);

        Category savedCategory = categoryRepository.save(category);

        return mapToResponse(savedCategory);
    }

    @Override
    public List<CategoryResponse> getCategories() {

        User currentUser = SecurityUtil.getCurrentUser();

        Business business = businessRepository.findByOwner(currentUser)
                .orElseThrow(() -> new BusinessException("Business not found."));

        return categoryRepository.findByBusinessOrderByDisplayOrderAsc(business)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CategoryResponse updateCategory(UUID categoryId, CategoryRequest request) {

        User currentUser = SecurityUtil.getCurrentUser();

        Business business = businessRepository.findByOwner(currentUser)
                .orElseThrow(() -> new BusinessException("Business not found."));

        Category category = categoryRepository.findByIdAndBusiness(categoryId, business)
                .orElseThrow(() -> new BusinessException("Category not found."));

        category.setName(request.getName());
        category.setDisplayOrder(request.getDisplayOrder());

        Category updatedCategory = categoryRepository.save(category);

        return mapToResponse(updatedCategory);
    }

    @Override
    public void deleteCategory(UUID categoryId) {

        User currentUser = SecurityUtil.getCurrentUser();

        Business business = businessRepository.findByOwner(currentUser)
                .orElseThrow(() -> new BusinessException("Business not found."));

        Category category = categoryRepository.findByIdAndBusiness(categoryId, business)
                .orElseThrow(() -> new BusinessException("Category not found."));

        categoryRepository.delete(category);
    }

    private CategoryResponse mapToResponse(Category category) {

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .displayOrder(category.getDisplayOrder())
                .active(category.getActive())
                .build();
    }
}
