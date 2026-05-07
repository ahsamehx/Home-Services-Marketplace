package com.homeservices.notification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String userType; // CUSTOMER, SERVICE_PROVIDER, ADMIN

    @Column(nullable = false)
    private String notificationType; // BOOKING_CONFIRMED, BOOKING_REJECTED, PAYMENT_FAILED

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    private Long bookingId;

    @Column(nullable = false)
    private Boolean isRead = false;

    @Column(nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    private LocalDateTime readDate;
}
