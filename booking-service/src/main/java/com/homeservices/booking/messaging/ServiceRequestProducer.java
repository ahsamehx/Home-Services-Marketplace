package com.homeservices.booking.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homeservices.booking.entity.ServiceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class ServiceRequestProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void publishRequestCreated(ServiceRequest request) {
        try {
            String message = objectMapper.writeValueAsString(request);
            rabbitTemplate.convertAndSend("request.exchange", "request.created", message);
            System.out.println("Published request.created for request: " + request.getId());
        } catch (Exception e) {
            System.err.println("Error publishing request.created: " + e.getMessage());
        }
    }

    public void publishRequestAccepted(ServiceRequest request, Long serviceProviderId) {
        try {
            String message = objectMapper.writeValueAsString(request);
            rabbitTemplate.convertAndSend("request.exchange", "request.accepted", message);
            System.out.println("Published request.accepted for request: " + request.getId());
        } catch (Exception e) {
            System.err.println("Error publishing request.accepted: " + e.getMessage());
        }
    }
}
