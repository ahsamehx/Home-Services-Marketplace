package com.homeservices.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.homeservices.booking.entity.ServiceRequest;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    List<ServiceRequest> findByCustomerId(Long customerId);
    List<ServiceRequest> findByStatus(String status);
    List<ServiceRequest> findByCategoryIdAndStatus(Long categoryId, String status);
    List<ServiceRequest> findByRequiredDateBetween(LocalDate startDate, LocalDate endDate);
}
