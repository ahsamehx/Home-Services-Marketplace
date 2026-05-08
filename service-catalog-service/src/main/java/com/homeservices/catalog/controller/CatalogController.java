package com.homeservices.catalog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.homeservices.catalog.entity.ServiceCategory;
import com.homeservices.catalog.entity.ServiceOffer;
import com.homeservices.catalog.service.CatalogService;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CatalogController {

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/categories")
    public ResponseEntity<?> addCategory(@RequestBody Map<String, Object> request) {
        Long adminId = request.get("adminId") != null ? ((Number) request.get("adminId")).longValue() : null;
        if (adminId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "adminId is required in request body"));
        }
        try {
            ResponseEntity<Map> userResponse = restTemplate.getForEntity(
                    "http://localhost:8080/api/users/" + adminId, Map.class);
            Map<String, Object> user = userResponse.getBody();
            if (user == null || !"ADMIN".equalsIgnoreCase((String) user.get("userType"))) {
                return ResponseEntity.status(403).body(Map.of("error", "Forbidden: admin only"));
            }
            ServiceCategory category = catalogService.addCategory(
                    (String) request.get("categoryName"),
                    (String) request.get("description")
            );
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            return ResponseEntity.status(403).body(Map.of("error", "Admin verification failed: " + e.getMessage()));
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.ok(catalogService.getAllCategories());
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(catalogService.getCategoryById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/offers")
    public ResponseEntity<?> createOffer(@RequestBody Map<String, Object> request) {
        try {
            Long serviceProviderId = ((Number) request.get("serviceProviderId")).longValue();
            Long categoryId = ((Number) request.get("categoryId")).longValue();
            Double price = ((Number) request.get("price")).doubleValue();
            LocalDate availableDate = LocalDate.parse((String) request.get("availableDate"));
            String description = (String) request.get("description");

            ServiceOffer offer = catalogService.createOffer(serviceProviderId, categoryId, 
                    price, availableDate, description);
            return ResponseEntity.ok(offer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/offers")
    public ResponseEntity<?> getAllOffers() {
        return ResponseEntity.ok(catalogService.getAllActiveOffers());
    }

    @GetMapping("/offers/{id}")
    public ResponseEntity<?> getOfferById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(catalogService.getOfferById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/offers/category/{categoryId}")
    public ResponseEntity<?> getOffersByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(catalogService.getOffersByCategory(categoryId));
    }

    @GetMapping("/offers/provider/{providerId}")
    public ResponseEntity<?> getOffersByProvider(@PathVariable Long providerId) {
        return ResponseEntity.ok(catalogService.getOffersByProvider(providerId));
    }

    @PutMapping("/offers/{id}")
    public ResponseEntity<?> updateOffer(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Double price = request.get("price") != null ? ((Number) request.get("price")).doubleValue() : null;
            LocalDate availableDate = request.get("availableDate") != null ? 
                    LocalDate.parse((String) request.get("availableDate")) : null;
            String description = (String) request.get("description");

            ServiceOffer offer = catalogService.updateOffer(id, price, availableDate, description);
            return ResponseEntity.ok(offer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/offers/{id}/deactivate")
    public ResponseEntity<?> deactivateOffer(@PathVariable Long id) {
        try {
            catalogService.deactivateOffer(id);
            return ResponseEntity.ok(Map.of("message", "Offer deactivated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/offers/{id}/activate")
    public ResponseEntity<?> activateOffer(@PathVariable Long id) {
        try {
            catalogService.activateOffer(id);
            return ResponseEntity.ok(Map.of("message", "Offer activated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
