package com.homeservices.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.homeservices.user.ejb.WalletBean;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
@CrossOrigin(origins = "*")
public class WalletController {

    @Autowired
    private WalletBean walletBean;

    @PostMapping("/validate")
    public ResponseEntity<?> validateBalance(@RequestBody Map<String, Object> request) {
        try {
            Long userId = ((Number) request.get("userId")).longValue();
            Double amount = ((Number) request.get("amount")).doubleValue();
            boolean valid = walletBean.validateBalance(userId, amount);
            return ResponseEntity.ok(Map.of("valid", valid));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/hold")
    public ResponseEntity<?> holdAmount(@RequestBody Map<String, Object> request) {
        try {
            Long userId = ((Number) request.get("userId")).longValue();
            Double amount = ((Number) request.get("amount")).doubleValue();
            Long bookingId = ((Number) request.get("bookingId")).longValue();
            walletBean.holdAmount(userId, amount, bookingId);
            return ResponseEntity.ok(Map.of("message", "Amount held successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmDeduction() {
        try {
            walletBean.confirmDeduction();
            return ResponseEntity.ok(Map.of("message", "Deduction confirmed"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/rollback")
    public ResponseEntity<?> rollbackDeduction() {
        try {
            walletBean.rollbackDeduction();
            return ResponseEntity.ok(Map.of("message", "Amount refunded"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/add-funds")
    public ResponseEntity<?> addFunds(@RequestBody Map<String, Object> request) {
        try {
            Long userId = ((Number) request.get("userId")).longValue();
            Double amount = ((Number) request.get("amount")).doubleValue();
            walletBean.addFunds(userId, amount);
            return ResponseEntity.ok(Map.of("message", "Funds added successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getBalance(@PathVariable Long userId) {
        try {
            Double balance = walletBean.getBalance(userId);
            return ResponseEntity.ok(Map.of("balance", balance));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
