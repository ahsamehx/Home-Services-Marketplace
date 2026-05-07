package com.homeservices.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    public Booking createBooking(Long customerId, Long offerId, Long serviceProviderId, 
                                  Double price, java.time.LocalDate serviceDate) throws Exception {
        if (!userServiceClient.validateBalance(customerId, price)) {
            throw new Exception("Insufficient wallet balance");
        }

        if (!userServiceClient.holdAmount(customerId, price, offerId)) {
            throw new Exception("Failed to hold amount in wallet");
        }

        Booking booking = new Booking();
        booking.setCustomerId(customerId);
        booking.setOfferId(offerId);
        booking.setServiceProviderId(serviceProviderId);
        booking.setPrice(price);
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
