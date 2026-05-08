package com.homeservices.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.homeservices.booking.entity.ServiceRequest;
import com.homeservices.booking.service.ServiceRequestService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/requests")
@CrossOrigin(origins = "*")
public class ServiceRequestController {

    @Autowired
    private ServiceRequestService serviceRequestService;

    @PostMapping
    public ResponseEntity<?> createRequest(@RequestBody Map<String, Object> request) {
        try {
            Long customerId = ((Number) request.get("customerId")).longValue();
            Long categoryId = ((Number) request.get("categoryId")).longValue();
            Double requiredPrice = ((Number) request.get("requiredPrice")).doubleValue();
            LocalDate requiredDate = LocalDate.parse((String) request.get("requiredDate"));
            String description = (String) request.get("description");

            ServiceRequest serviceRequest = serviceRequestService.createRequest(
                    customerId, categoryId, requiredPrice, requiredDate, description);
            return ResponseEntity.ok(serviceRequest);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRequest(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(serviceRequestService.getRequestById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getCustomerRequests(@PathVariable Long customerId) {
        return ResponseEntity.ok(serviceRequestService.getCustomerRequests(customerId));
    }

    @GetMapping("/category/{categoryId}/open")
    public ResponseEntity<?> getOpenRequestsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(serviceRequestService.getOpenRequestsByCategory(categoryId));
    }

    @GetMapping("/{id}/matching-offers")
    public ResponseEntity<?> getMatchingOffers(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(serviceRequestService.findMatchingOffers(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<?> acceptRequest(@PathVariable Long id, 
                                           @RequestParam(value = "serviceProviderId") Long serviceProviderId,
                                           @RequestParam(value = "offerId") Long offerId) {
        try {
            return ResponseEntity.ok(serviceRequestService.acceptRequest(id, serviceProviderId, offerId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectRequest(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(serviceRequestService.rejectRequest(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
