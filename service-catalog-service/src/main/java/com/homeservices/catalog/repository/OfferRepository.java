package com.homeservices.catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.homeservices.catalog.entity.ServiceOffer;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface OfferRepository extends JpaRepository<ServiceOffer, Long> {
    List<ServiceOffer> findByServiceProviderId(Long serviceProviderId);
    List<ServiceOffer> findByCategoryId(Long categoryId);
    List<ServiceOffer> findByActive(boolean active);
    List<ServiceOffer> findByAvailableDateBetween(LocalDate startDate, LocalDate endDate);
    List<ServiceOffer> findByCategoryIdAndActive(Long categoryId, boolean active);
}
