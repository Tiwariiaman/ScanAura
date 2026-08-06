package com.scanaura.catalog.repository;

import com.scanaura.business.entity.Business;
import com.scanaura.catalog.entity.Catalog;
import com.scanaura.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CatalogRepository extends JpaRepository<Catalog, UUID> {

    List<Catalog> findByBusinessOrderByDisplayOrderAsc(Business business);

    List<Catalog> findByBusinessAndCategoryOrderByDisplayOrderAsc(
            Business business,
            Category category
    );

    List<Catalog> findByBusinessAndCategoryIsNullOrderByDisplayOrderAsc(
            Business business
    );

    Optional<Catalog> findByIdAndBusiness(
            UUID id,
            Business business
    );

    List<Catalog> findByBusinessAndCategory_IdOrderByDisplayOrderAsc(
            Business business,
            UUID categoryId
    );
}