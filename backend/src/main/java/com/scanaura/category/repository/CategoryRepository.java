package com.scanaura.category.repository;

import com.scanaura.business.entity.Business;
import com.scanaura.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findByBusinessOrderByDisplayOrderAsc(Business business);

    Optional<Category> findByIdAndBusiness(UUID id, Business business);

    boolean existsByBusinessAndName(Business business, String name);

}
