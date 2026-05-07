package com.homeservices.booking.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class BookingListener {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "booking.confirmed")
    public void handleBookingConfirmed(String message) {
        try {
            System.out.println("Received booking.confirmed: " + message);
        } catch (Exception e) {
            System.err.println("Error handling booking.confirmed: " + e.getMessage());
        }
    }

    @RabbitListener(queues = "booking.rejected")
    public void handleBookingRejected(String message) {
        try {
            System.out.println("Received booking.rejected: " + message);
        } catch (Exception e) {
            System.err.println("Error handling booking.rejected: " + e.getMessage());
        }
    }

    @RabbitListener(queues = "payment.failed")
    public void handlePaymentFailed(String message) {
        try {
            System.out.println("Received payment.failed: " + message);
        } catch (Exception e) {
            System.err.println("Error handling payment.failed: " + e.getMessage());
        }
    }
}
