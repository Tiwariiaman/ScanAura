package com.scanaura.category.controller;

import com.scanaura.category.dto.CategoryRequest;
import com.scanaura.category.dto.CategoryResponse;
import com.scanaura.category.service.CategoryService;
import com.scanaura.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CategoryRequest request) {

        CategoryResponse response = categoryService.createCategory(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        "Category created successfully.",
                        response
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories() {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Categories fetched successfully.",
                        categoryService.getCategories()
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryRequest request) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Category updated successfully.",
                        categoryService.updateCategory(id, request)
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCategory(
            @PathVariable UUID id) {

        categoryService.deleteCategory(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Category deleted successfully.",
                        "Deleted"
                )
        );
    }
}