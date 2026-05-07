package com.homeservices.booking.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Component
public class UserServiceClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String userServiceUrl = "http://localhost:8080";

    public boolean validateBalance(Long userId, Double amount) {
        try {
            Map<String, Object> request = Map.of(
                    "userId", userId,
                    "amount", amount
            );
            Map<String, Object> response = restTemplate.postForObject(
                    userServiceUrl + "/api/wallet/validate",
                    request,
                    Map.class
            );
            return (boolean) response.get("valid");
        } catch (Exception e) {
            System.err.println("Error validating balance: " + e.getMessage());
            return false;
        }
    }

    public boolean holdAmount(Long userId, Double amount, Long bookingId) {
        try {
            Map<String, Object> request = Map.of(
                    "userId", userId,
                    "amount", amount,
                    "bookingId", bookingId
            );
            restTemplate.postForObject(
                    userServiceUrl + "/api/wallet/hold",
                    request,
                    Map.class
            );
            return true;
        } catch (Exception e) {
            System.err.println("Error holding amount: " + e.getMessage());
            return false;
        }
    }

    public boolean confirmDeduction(Long userId) {
        try {
            restTemplate.postForObject(
                    userServiceUrl + "/api/wallet/confirm",
                    Map.of("userId", userId),
                    Map.class
            );
            return true;
        } catch (Exception e) {
            System.err.println("Error confirming deduction: " + e.getMessage());
            return false;
        }
    }

    public boolean rollbackDeduction(Long userId) {
        try {
            restTemplate.postForObject(
                    userServiceUrl + "/api/wallet/rollback",
                    Map.of("userId", userId),
                    Map.class
            );
            return true;
        } catch (Exception e) {
            System.err.println("Error rolling back deduction: " + e.getMessage());
            return false;
        }
    }
}
