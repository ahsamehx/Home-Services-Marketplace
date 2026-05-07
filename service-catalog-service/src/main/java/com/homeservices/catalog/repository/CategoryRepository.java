package com.homeservices.catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.homeservices.catalog.entity.ServiceCategory;

@Repository
public interface CategoryRepository extends JpaRepository<ServiceCategory, Long> {
    ServiceCategory findByCategoryName(String categoryName);
}
