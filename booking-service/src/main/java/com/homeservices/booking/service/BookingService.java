package com.homeservices.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.homeservices.booking.entity.Booking;
import com.homeservices.booking.entity.BookingHistory;
import com.homeservices.booking.repository.BookingRepository;
import com.homeservices.booking.repository.BookingHistoryRepository;
import com.homeservices.booking.client.UserServiceClient;
import com.homeservices.booking.messaging.BookingProducer;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingHistoryRepository bookingHistoryRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private BookingProducer bookingProducer;

    @Autowired
    private RestTemplate restTemplate;

    public Booking createBooking(Long customerId, Long offerId, Long serviceProviderId,
                                  java.time.LocalDate serviceDate) throws Exception {
        // Fetch offer from catalog-service to verify existence, provider and price
        Double offerPrice;
        Long offerProviderId;
        String availableDateStr;
        try {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> offer = restTemplate.getForObject(
                    "http://localhost:8081/api/offers/" + offerId, java.util.Map.class);
            if (offer == null) {
                throw new Exception("Offer not found");
            }
            if (offer.get("price") == null || offer.get("serviceProviderId") == null || offer.get("availableDate") == null) {
                throw new Exception("Offer missing required fields");
            }
            offerPrice = ((Number) offer.get("price")).doubleValue();
            offerProviderId = ((Number) offer.get("serviceProviderId")).longValue();
            availableDateStr = (String) offer.get("availableDate");
        } catch (Exception e) {
            throw new Exception("Failed to fetch offer: " + e.getMessage());
        }

        // Validate provided serviceProviderId matches the offer's provider
        if (!offerProviderId.equals(serviceProviderId)) {
            throw new Exception("Service provider does not match the offer");
        }

        // Validate serviceDate matches offer's available date
        java.time.LocalDate offerAvailableDate = java.time.LocalDate.parse(availableDateStr);
        if (!offerAvailableDate.equals(serviceDate)) {
            throw new Exception("Service date does not match offer's available date");
        }

        // Use the offer's price for validation/holding
        if (!userServiceClient.validateBalance(customerId, offerPrice)) {
            bookingProducer.publishInsufficientFunds(customerId, offerPrice);
            throw new Exception("Insufficient wallet balance");
        }

        if (!userServiceClient.holdAmount(customerId, offerPrice, offerId)) {
            throw new Exception("Failed to hold amount in wallet");
        }

        Booking booking = new Booking();
        booking.setCustomerId(customerId);
        booking.setOfferId(offerId);
        booking.setServiceProviderId(serviceProviderId);
        booking.setPrice(offerPrice);
        booking.setServiceDate(serviceDate);
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus("PENDING");
        booking.setCreatedDate(LocalDateTime.now());

        Booking savedBooking = bookingRepository.save(booking);

        recordHistory(savedBooking.getId(), "PENDING");

        bookingProducer.publishBookingCreated(savedBooking);

        return savedBooking;
    }

    public Booking confirmBooking(Long bookingId) throws Exception {
        Booking booking = getBookingById(bookingId);

        if (!booking.getStatus().equals("PENDING")) {
            throw new Exception("Booking is not in PENDING status");
        }

        userServiceClient.confirmDeduction(booking.getCustomerId());

        booking.setStatus("CONFIRMED");
        booking.setUpdatedDate(LocalDateTime.now());
        Booking updatedBooking = bookingRepository.save(booking);

        recordHistory(bookingId, "CONFIRMED");

        bookingProducer.publishBookingConfirmed(updatedBooking);

        return updatedBooking;
    }

    public Booking rejectBooking(Long bookingId) throws Exception {
        Booking booking = getBookingById(bookingId);

        if (!booking.getStatus().equals("PENDING")) {
            throw new Exception("Booking is not in PENDING status");
        }

        userServiceClient.rollbackDeduction(booking.getCustomerId());

        booking.setStatus("REJECTED");
        booking.setUpdatedDate(LocalDateTime.now());
        Booking updatedBooking = bookingRepository.save(booking);

        recordHistory(bookingId, "REJECTED");

        bookingProducer.publishBookingRejected(updatedBooking);
        bookingProducer.publishPaymentFailed(updatedBooking);

        return updatedBooking;
    }

    public Booking getBookingById(Long bookingId) throws Exception {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new Exception("Booking not found"));
    }

    public List<Booking> getCustomerBookings(Long customerId) {
        return bookingRepository.findByCustomerId(customerId);
    }

    public List<Booking> getProviderBookings(Long serviceProviderId) {
        return bookingRepository.findByServiceProviderId(serviceProviderId);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<BookingHistory> getBookingHistory(Long bookingId) {
        return bookingHistoryRepository.findByBookingId(bookingId);
    }

    private void recordHistory(Long bookingId, String status) {
        BookingHistory history = new BookingHistory();
        history.setBookingId(bookingId);
        history.setStatus(status);
        history.setStatusChangedDate(LocalDateTime.now());
        bookingHistoryRepository.save(history);
    }
}
