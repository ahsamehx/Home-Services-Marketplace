package com.homeservices.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.homeservices.booking.entity.ServiceRequest;
import com.homeservices.booking.entity.Booking;
import com.homeservices.booking.repository.ServiceRequestRepository;
import com.homeservices.booking.repository.BookingRepository;
import com.homeservices.booking.messaging.ServiceRequestProducer;
import com.homeservices.booking.messaging.BookingProducer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ServiceRequestService {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ServiceRequestProducer serviceRequestProducer;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingProducer bookingProducer;

    public ServiceRequest createRequest(Long customerId, Long categoryId, Double requiredPrice, 
                                        LocalDate requiredDate, String description) throws Exception {
        ServiceRequest request = new ServiceRequest();
        request.setCustomerId(customerId);
        request.setCategoryId(categoryId);
        request.setRequiredPrice(requiredPrice);
        request.setRequiredDate(requiredDate);
        request.setDescription(description);
        request.setStatus("OPEN");
        request.setCreatedDate(LocalDateTime.now());

        ServiceRequest savedRequest = serviceRequestRepository.save(request);
        serviceRequestProducer.publishRequestCreated(savedRequest);

        return savedRequest;
    }

    public ServiceRequest getRequestById(Long requestId) throws Exception {
        return serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new Exception("Service request not found"));
    }

    public List<ServiceRequest> getCustomerRequests(Long customerId) {
        return serviceRequestRepository.findByCustomerId(customerId);
    }

    public List<ServiceRequest> getOpenRequestsByCategory(Long categoryId) {
        return serviceRequestRepository.findByCategoryIdAndStatus(categoryId, "OPEN");
    }

    public List<Map> findMatchingOffers(Long requestId) throws Exception {
        ServiceRequest request = getRequestById(requestId);

        try {
            Map[] offersArray = restTemplate.getForObject(
                    "http://localhost:8081/api/offers/category/" + request.getCategoryId(), 
                    Map[].class);

            if (offersArray == null) {
                return List.of();
            }

            List<Map> matchingOffers = new java.util.ArrayList<>();
            for (Map offer : offersArray) {
                Double offerPrice = ((Number) offer.get("price")).doubleValue();
                String availableDateStr = (String) offer.get("availableDate");
                LocalDate availableDate = LocalDate.parse(availableDateStr);

                if (offerPrice <= request.getRequiredPrice() && availableDate.equals(request.getRequiredDate())) {
                    matchingOffers.add(offer);
                }
            }
            return matchingOffers;
        } catch (Exception e) {
            throw new Exception("Failed to fetch offers: " + e.getMessage());
        }
    }

    public ServiceRequest acceptRequest(Long requestId, Long serviceProviderId, Long offerId) throws Exception {
        ServiceRequest request = getRequestById(requestId);
        
        if (!"OPEN".equals(request.getStatus())) {
            throw new Exception("Request is not open for acceptance");
        }

        request.setStatus("ACCEPTED");
        request.setUpdatedDate(LocalDateTime.now());
        ServiceRequest savedRequest = serviceRequestRepository.save(request);

        Booking booking = new Booking();
        booking.setCustomerId(request.getCustomerId());
        booking.setOfferId(offerId);
        booking.setServiceProviderId(serviceProviderId);
        booking.setPrice(request.getRequiredPrice());
        booking.setServiceDate(request.getRequiredDate());
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus("PENDING");
        booking.setCreatedDate(LocalDateTime.now());

        Booking savedBooking = bookingRepository.save(booking);
        bookingProducer.publishBookingCreated(savedBooking);

        serviceRequestProducer.publishRequestAccepted(savedRequest, serviceProviderId);

        return savedRequest;
    }

    public ServiceRequest rejectRequest(Long requestId) throws Exception {
        ServiceRequest request = getRequestById(requestId);
        request.setStatus("REJECTED");
        request.setUpdatedDate(LocalDateTime.now());
        return serviceRequestRepository.save(request);
    }
}
