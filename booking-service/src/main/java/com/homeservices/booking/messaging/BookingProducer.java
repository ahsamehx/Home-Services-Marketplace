package com.homeservices.booking.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.homeservices.booking.entity.Booking;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class BookingProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void publishBookingCreated(Booking booking) {
        try {
            String message = objectMapper.writeValueAsString(booking);
            rabbitTemplate.convertAndSend(
                    "booking.exchange",
                    "booking.created",
                    message
            );
            System.out.println("Published booking.created: " + booking.getId());
        } catch (Exception e) {
            System.err.println("Error publishing booking.created: " + e.getMessage());
        }
    }

    public void publishBookingConfirmed(Booking booking) {
        try {
            String message = objectMapper.writeValueAsString(booking);
            rabbitTemplate.convertAndSend(
                    "booking.exchange",
                    "booking.confirmed",
                    message
            );
            System.out.println("Published booking.confirmed: " + booking.getId());
        } catch (Exception e) {
            System.err.println("Error publishing booking.confirmed: " + e.getMessage());
        }
    }

    public void publishBookingRejected(Booking booking) {
        try {
            String message = objectMapper.writeValueAsString(booking);
            rabbitTemplate.convertAndSend(
                    "booking.exchange",
                    "booking.rejected",
                    message
            );
            System.out.println("Published booking.rejected: " + booking.getId());
        } catch (Exception e) {
            System.err.println("Error publishing booking.rejected: " + e.getMessage());
        }
    }

    public void publishPaymentFailed(Booking booking) {
        try {
            String message = objectMapper.writeValueAsString(booking);
            rabbitTemplate.convertAndSend(
                    "payment.exchange",
                    "payment.failed",
                    message
            );
            System.out.println("Published payment.failed: " + booking.getId());
        } catch (Exception e) {
            System.err.println("Error publishing payment.failed: " + e.getMessage());
        }
    }

    public void publishInsufficientFunds(Long customerId, Double requiredAmount) {
        try {
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("customerId", customerId);
            data.put("requiredAmount", requiredAmount);
            String message = objectMapper.writeValueAsString(data);
            rabbitTemplate.convertAndSend(
                    "booking.exchange",
                    "booking.insufficient_funds",
                    message
            );
            System.out.println("Published booking.insufficient_funds for customer: " + customerId);
        } catch (Exception e) {
            System.err.println("Error publishing booking.insufficient_funds: " + e.getMessage());
        }
    }
}
