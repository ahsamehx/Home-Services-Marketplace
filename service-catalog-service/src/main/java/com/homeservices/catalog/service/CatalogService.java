package com.homeservices.catalog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.homeservices.catalog.entity.ServiceCategory;
import com.homeservices.catalog.entity.ServiceOffer;
import com.homeservices.catalog.repository.CategoryRepository;
import com.homeservices.catalog.repository.OfferRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CatalogService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OfferRepository offerRepository;

    public ServiceCategory addCategory(String categoryName, String description) throws Exception {
        if (categoryRepository.findByCategoryName(categoryName) != null) {
            throw new Exception("Category already exists");
        }
        
        ServiceCategory category = new ServiceCategory();
        category.setCategoryName(categoryName);
        category.setDescription(description);
        category.setCreatedDate(LocalDateTime.now());
        
        return categoryRepository.save(category);
    }

    public List<ServiceCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    public ServiceCategory getCategoryById(Long categoryId) throws Exception {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new Exception("Category not found"));
    }

    public ServiceOffer createOffer(Long serviceProviderId, Long categoryId, Double price, 
                                    LocalDate availableDate, String description) throws Exception {
        getCategoryById(categoryId);

        ServiceOffer offer = new ServiceOffer();
        offer.setServiceProviderId(serviceProviderId);
        offer.setCategoryId(categoryId);
        offer.setPrice(price);
        offer.setAvailableDate(availableDate);
        offer.setDescription(description);
        offer.setActive(true);
        offer.setCreatedDate(LocalDateTime.now());

        return offerRepository.save(offer);
    }

    public ServiceOffer getOfferById(Long offerId) throws Exception {
        return offerRepository.findById(offerId)
                .orElseThrow(() -> new Exception("Offer not found"));
    }

    public List<ServiceOffer> getOffersByCategory(Long categoryId) {
        return offerRepository.findByCategoryIdAndActive(categoryId, true);
    }

    public List<ServiceOffer> getOffersByProvider(Long serviceProviderId) {
        return offerRepository.findByServiceProviderId(serviceProviderId);
    }

    public List<ServiceOffer> getAllActiveOffers() {
        return offerRepository.findByActive(true);
    }

    public ServiceOffer updateOffer(Long offerId, Double price, LocalDate availableDate, 
                                     String description) throws Exception {
        ServiceOffer offer = getOfferById(offerId);
        
        if (price != null) offer.setPrice(price);
        if (availableDate != null) offer.setAvailableDate(availableDate);
        if (description != null) offer.setDescription(description);
        offer.setUpdatedDate(LocalDateTime.now());

        return offerRepository.save(offer);
    }

    public void deactivateOffer(Long offerId) throws Exception {
        ServiceOffer offer = getOfferById(offerId);
        offer.setActive(false);
        offer.setUpdatedDate(LocalDateTime.now());
        offerRepository.save(offer);
    }

    public void activateOffer(Long offerId) throws Exception {
        ServiceOffer offer = getOfferById(offerId);
        offer.setActive(true);
        offer.setUpdatedDate(LocalDateTime.now());
        offerRepository.save(offer);
    }
}
