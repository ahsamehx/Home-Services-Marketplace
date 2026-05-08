package com.homeservices.notification.messaging;

import com.homeservices.notification.config.RabbitMQConfig;
import com.homeservices.notification.client.CatalogServiceClient;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.homeservices.notification.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.List;

@Component
public class NotificationListener {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CatalogServiceClient catalogServiceClient;

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
            Long customerId = ((Number) booking.get("customerId")).longValue();
            Long bookingId = ((Number) booking.get("id")).longValue();
            Double price = ((Number) booking.get("price")).doubleValue();

            String customerMsg = "Payment failed for your booking (ID: " + bookingId + "). Amount: $" + price + ". Please try again.";
            notificationService.createNotification(customerId, "CUSTOMER", "PAYMENT_FAILED", customerMsg, bookingId);

            String adminMsg = "Payment failed for booking (ID: " + bookingId + "). Amount: $" + price + ". Manual review may be required.";
            notificationService.createNotification(-1L, "ADMIN", "PAYMENT_FAILED", adminMsg, bookingId);

            System.out.println("Payment failed notifications sent for booking: " + bookingId);
        } catch (Exception e) {
            System.err.println("Error handling payment.failed: " + e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitMQConfig.REQUEST_CREATED_NOTIFICATION_QUEUE)
    public void handleRequestCreated(String message) {
        try {
            Map<String, Object> request = objectMapper.readValue(message, Map.class);
            Long requestId = ((Number) request.get("id")).longValue();
            Long categoryId = ((Number) request.get("categoryId")).longValue();
            Double requiredPrice = ((Number) request.get("requiredPrice")).doubleValue();

            List<Long> providerIds = catalogServiceClient.getProvidersInCategory(categoryId);
            
            String msg = "New service request (ID: " + requestId + ") in your category for $" + requiredPrice;
            
            if (providerIds.isEmpty()) {
                System.out.println("No providers found in category " + categoryId + " for request: " + requestId);
            } else {
                for (Long providerId : providerIds) {
                    notificationService.createNotification(providerId, "SERVICE_PROVIDER", "REQUEST_CREATED", msg, requestId);
                }
                System.out.println("New request notifications sent to " + providerIds.size() + " providers for request: " + requestId);
            }
        } catch (Exception e) {
            System.err.println("Error handling request.created: " + e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitMQConfig.REQUEST_ACCEPTED_NOTIFICATION_QUEUE)
    public void handleRequestAccepted(String message) {
        try {
            Map<String, Object> request = objectMapper.readValue(message, Map.class);
            Long requestId = ((Number) request.get("id")).longValue();
            Long customerId = ((Number) request.get("customerId")).longValue();

            String msg = "A service provider has accepted your request (ID: " + requestId + "). A booking has been created.";
            notificationService.createNotification(customerId, "CUSTOMER", "REQUEST_ACCEPTED", msg, requestId);

            System.out.println("Request accepted notification sent to customer for request: " + requestId);
        } catch (Exception e) {
            System.err.println("Error handling request.accepted: " + e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitMQConfig.INSUFFICIENT_FUNDS_NOTIFICATION_QUEUE)
    public void handleInsufficientFunds(String message) {
        try {
            Map<String, Object> data = objectMapper.readValue(message, Map.class);
            Long customerId = ((Number) data.get("customerId")).longValue();
            Double requiredAmount = ((Number) data.get("requiredAmount")).doubleValue();

            String customerMsg = "Booking failed: Insufficient wallet balance. Required: $" + requiredAmount;
            notificationService.createNotification(customerId, "CUSTOMER", "PAYMENT_FAILED", customerMsg, null);

            String adminMsg = "Booking failed for customer ID " + customerId + ": Insufficient wallet balance. Required: $" + requiredAmount;
            notificationService.createNotification(-1L, "ADMIN", "PAYMENT_FAILED", adminMsg, null);

            System.out.println("Insufficient funds notifications sent for customer: " + customerId);
        } catch (Exception e) {
            System.err.println("Error handling booking.insufficient_funds: " + e.getMessage());
        }
    }
}
