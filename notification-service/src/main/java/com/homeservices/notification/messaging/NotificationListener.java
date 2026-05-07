package com.homeservices.notification.messaging;

import com.homeservices.notification.config.RabbitMQConfig;
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

    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitMQConfig.BOOKING_CONFIRMED_NOTIFICATION_QUEUE)
    public void handleBookingConfirmed(String message) {
        try {
            Map<String, Object> booking = objectMapper.readValue(message, Map.class);
            Long customerId = ((Number) booking.get("customerId")).longValue();
            Long serviceProviderId = ((Number) booking.get("serviceProviderId")).longValue();
            Long bookingId = ((Number) booking.get("id")).longValue();
            Double price = ((Number) booking.get("price")).doubleValue();

            String customerMsg = "Your booking (ID: " + bookingId + ") has been confirmed. Amount: $" + price;
            notificationService.createNotification(customerId, "CUSTOMER", "BOOKING_CONFIRMED", customerMsg, bookingId);

            String providerMsg = "Booking (ID: " + bookingId + ") has been confirmed. Amount: $" + price;
            notificationService.createNotification(serviceProviderId, "SERVICE_PROVIDER", "BOOKING_CONFIRMED", providerMsg, bookingId);

            System.out.println("Booking confirmed notifications sent for booking: " + bookingId);
        } catch (Exception e) {
            System.err.println("Error handling booking.confirmed: " + e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitMQConfig.BOOKING_REJECTED_NOTIFICATION_QUEUE)
    public void handleBookingRejected(String message) {
        try {
            Map<String, Object> booking = objectMapper.readValue(message, Map.class);
            Long customerId = ((Number) booking.get("customerId")).longValue();
            Long serviceProviderId = ((Number) booking.get("serviceProviderId")).longValue();
            Long bookingId = ((Number) booking.get("id")).longValue();
            Double price = ((Number) booking.get("price")).doubleValue();

            String customerMsg = "Your booking (ID: " + bookingId + ") was rejected. Refund amount: $" + price;
            notificationService.createNotification(customerId, "CUSTOMER", "BOOKING_REJECTED", customerMsg, bookingId);

            String providerMsg = "You rejected booking (ID: " + bookingId + "). Refund amount to customer: $" + price;
            notificationService.createNotification(serviceProviderId, "SERVICE_PROVIDER", "BOOKING_REJECTED", providerMsg, bookingId);

            System.out.println("Booking rejected notifications sent for booking: " + bookingId);
        } catch (Exception e) {
            System.err.println("Error handling booking.rejected: " + e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_FAILED_NOTIFICATION_QUEUE)
    public void handlePaymentFailed(String message) {
        try {
            Map<String, Object> booking = objectMapper.readValue(message, Map.class);
            Long bookingId = ((Number) booking.get("id")).longValue();
            Double price = ((Number) booking.get("price")).doubleValue();

            String msg = "Payment failed for booking (ID: " + bookingId + "). Amount: $" + price + ". Manual review may be required.";
            notificationService.createNotification(-1L, "ADMIN", "PAYMENT_FAILED", msg, bookingId);

            System.out.println("Payment failed notification sent to admin for booking: " + bookingId);
        } catch (Exception e) {
            System.err.println("Error handling payment.failed: " + e.getMessage());
        }
    }
}
