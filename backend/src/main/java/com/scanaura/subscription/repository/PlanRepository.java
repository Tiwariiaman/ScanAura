package com.scanaura.subscription.repository;

import com.scanaura.subscription.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlanRepository extends JpaRepository<Plan, UUID> {

    Optional<Plan> findByNameIgnoreCase(String name);

    List<Plan> findByActiveTrueOrderByNameAsc();
}