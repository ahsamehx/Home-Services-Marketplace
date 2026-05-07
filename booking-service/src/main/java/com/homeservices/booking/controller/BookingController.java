package com.homeservices.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.homeservices.booking.entity.Booking;
import com.homeservices.booking.service.BookingService;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Map<String, Object> request) {
        try {
            Long customerId = ((Number) request.get("customerId")).longValue();
            Long offerId = ((Number) request.get("offerId")).longValue();
            Long serviceProviderId = ((Number) request.get("serviceProviderId")).longValue();
            Double price = ((Number) request.get("price")).doubleValue();
            LocalDate serviceDate = LocalDate.parse((String) request.get("serviceDate"));

            Booking booking = bookingService.createBooking(customerId, offerId, 
                    serviceProviderId, price, serviceDate);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBooking(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(bookingService.getBookingById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getCustomerBookings(@PathVariable Long customerId) {
        return ResponseEntity.ok(bookingService.getCustomerBookings(customerId));
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<?> getProviderBookings(@PathVariable Long providerId) {
        return ResponseEntity.ok(bookingService.getProviderBookings(providerId));
    }

    @GetMapping("/history/all")
    public ResponseEntity<?> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<?> getBookingHistory(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingHistory(id));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<?> confirmBooking(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(bookingService.confirmBooking(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectBooking(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(bookingService.rejectBooking(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
