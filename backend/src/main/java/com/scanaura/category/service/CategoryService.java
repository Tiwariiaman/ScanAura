package com.scanaura.category.service;

import com.scanaura.category.dto.CategoryRequest;
import com.scanaura.category.dto.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    CategoryResponse createCategory(CategoryRequest request);

    List<CategoryResponse> getCategories();

    CategoryResponse updateCategory(UUID categoryId, CategoryRequest request);

    void deleteCategory(UUID categoryId);

}