package com.homeservices.notification.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.homeservices.notification.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

@Component
public class NotificationListener {

    @Autowired
    private NotificationService notificationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "booking.confirmed")
    public void handleBookingConfirmed(String message) {
        try {
            Map<String, Object> booking = objectMapper.readValue(message, Map.class);
            Long customerId = ((Number) booking.get("customerId")).longValue();
            Long serviceProviderId = ((Number) booking.get("serviceProviderId")).longValue();
            Long bookingId = ((Number) booking.get("id")).longValue();
            Double price = ((Number) booking.get("price")).doubleValue();

            // Notify customer
            String customerMsg = "Your booking (ID: " + bookingId + ") has been confirmed. Amount: $" + price;
            notificationService.createNotification(customerId, "CUSTOMER", "BOOKING_CONFIRMED", customerMsg, bookingId);

            // Notify service provider
            String providerMsg = "Booking (ID: " + bookingId + ") has been confirmed. Amount: $" + price;
            notificationService.createNotification(serviceProviderId, "SERVICE_PROVIDER", "BOOKING_CONFIRMED", providerMsg, bookingId);

            System.out.println("✓ Booking confirmed notifications sent for booking: " + bookingId);
        } catch (Exception e) {
            System.err.println("Error handling booking.confirmed: " + e.getMessage());
        }
    }

    @RabbitListener(queues = "booking.rejected")
    public void handleBookingRejected(String message) {
        try {
            Map<String, Object> booking = objectMapper.readValue(message, Map.class);
            Long customerId = ((Number) booking.get("customerId")).longValue();
            Long bookingId = ((Number) booking.get("id")).longValue();
            Double price = ((Number) booking.get("price")).doubleValue();

            // Notify customer
            String msg = "Your booking (ID: " + bookingId + ") was rejected. Refund amount: $" + price;
            notificationService.createNotification(customerId, "CUSTOMER", "BOOKING_REJECTED", msg, bookingId);

            System.out.println("✓ Booking rejected notification sent for booking: " + bookingId);
        } catch (Exception e) {
            System.err.println("Error handling booking.rejected: " + e.getMessage());
        }
    }

    @RabbitListener(queues = "payment.failed")
    public void handlePaymentFailed(String message) {
        try {
            Map<String, Object> booking = objectMapper.readValue(message, Map.class);
            Long bookingId = ((Number) booking.get("id")).longValue();
            Double price = ((Number) booking.get("price")).doubleValue();

            // Notify admin (userId = -1 for admin, or use admin userId if available)
            String msg = "Payment failed for booking (ID: " + bookingId + "). Amount: $" + price + ". Manual review may be required.";
            notificationService.createNotification(-1L, "ADMIN", "PAYMENT_FAILED", msg, bookingId);

            System.out.println("✓ Payment failed notification sent to admin for booking: " + bookingId);
        } catch (Exception e) {
            System.err.println("Error handling payment.failed: " + e.getMessage());
        }
    }
}
