package com.homeservices.notification.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.homeservices.notification.entity.Notification;
import com.homeservices.notification.service.NotificationService;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<?> getUserUnreadNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUserUnreadNotifications(userId));
    }

    @GetMapping("/type/{userType}")
    public ResponseEntity<?> getNotificationsByType(@PathVariable String userType) {
        return ResponseEntity.ok(notificationService.getNotificationsByUserType(userType));
    }

    @GetMapping("/type/{userType}/unread")
    public ResponseEntity<?> getUnreadNotificationsByType(@PathVariable String userType) {
        return ResponseEntity.ok(notificationService.getUnreadNotificationsByUserType(userType));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<?> getBookingNotifications(@PathVariable Long bookingId) {
        return ResponseEntity.ok(notificationService.getBookingNotifications(bookingId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNotification(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(notificationService.getNotificationById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(notificationService.markAsRead(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/user/{userId}/read-all")
    public ResponseEntity<?> markAllAsRead(@PathVariable Long userId) {
        try {
            notificationService.markAllAsRead(userId);
            return ResponseEntity.ok(Map.of("message", "All notifications marked as read"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
