package com.homeservices.notification.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CatalogServiceClient {

    @Autowired
    private RestTemplate restTemplate;

    private static final String CATALOG_SERVICE_URL = "http://localhost:8081/api/offers/category";

    public List<Long> getProvidersInCategory(Long categoryId) {
        try {
            String url = CATALOG_SERVICE_URL + "/" + categoryId;
            Map[] offers = restTemplate.getForObject(url, Map[].class);
            
            List<Long> providerIds = new ArrayList<>();
            if (offers != null) {
                for (Map offer : offers) {
                    Number providerId = (Number) offer.get("serviceProviderId");
                    if (providerId != null) {
                        Long pId = providerId.longValue();
                        if (!providerIds.contains(pId)) {
                            providerIds.add(pId);
                        }
                    }
                }
            }
            return providerIds;
        } catch (Exception e) {
            System.err.println("Error fetching providers from catalog-service: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
