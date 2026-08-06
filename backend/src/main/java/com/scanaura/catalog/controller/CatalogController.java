package com.scanaura.catalog.controller;

import com.scanaura.catalog.dto.CatalogRequest;
import com.scanaura.catalog.dto.CatalogResponse;
import com.scanaura.catalog.service.CatalogService;
import com.scanaura.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/catalog")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;

    @PostMapping
    public ResponseEntity<ApiResponse<CatalogResponse>> createCatalog(
            @Valid @RequestBody CatalogRequest request) {

        CatalogResponse response = catalogService.createCatalog(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        "Catalog item created successfully.",
                        response
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CatalogResponse>>> getCatalogs(
            @RequestParam(required = false) UUID categoryId) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Catalog fetched successfully.",
                        catalogService.getCatalogs(categoryId)
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CatalogResponse>> getCatalog(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Catalog fetched successfully.",
                        catalogService.getCatalog(id)
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CatalogResponse>> updateCatalog(
            @PathVariable UUID id,
            @Valid @RequestBody CatalogRequest request) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Catalog updated successfully.",
                        catalogService.updateCatalog(id, request)
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCatalog(
            @PathVariable UUID id) {

        catalogService.deleteCatalog(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Catalog deleted successfully.",
                        "Deleted"
                )
        );
    }
}