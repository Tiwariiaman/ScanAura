package com.scanaura.catalog.entity;

import com.scanaura.business.entity.Business;
import com.scanaura.category.entity.Category;
import com.scanaura.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "catalogs")
@Getter
@Setter
public class Catalog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    private String imageUrl;

    @Column(nullable = false)
    private Boolean veg = true;

    @Column(nullable = false)
    private Boolean available = true;

    @Column(nullable = false)
    private Integer displayOrder = 0;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private Boolean bestSeller = false;

    @Column(nullable = false)
    private Boolean recommended = false;



}