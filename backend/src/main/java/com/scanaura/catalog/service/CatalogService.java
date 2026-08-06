package com.scanaura.catalog.service;

import com.scanaura.catalog.dto.CatalogRequest;
import com.scanaura.catalog.dto.CatalogResponse;

import java.util.List;
import java.util.UUID;

public interface CatalogService {

    CatalogResponse createCatalog(CatalogRequest request);

    CatalogResponse getCatalog(UUID catalogId);

    CatalogResponse updateCatalog(UUID catalogId, CatalogRequest request);

    void deleteCatalog(UUID catalogId);

    List<CatalogResponse> getCatalogs(UUID categoryId);

}