package com.homeservices.booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false)
    private Long offerId;

    @Column(nullable = false)
    private Long serviceProviderId;

    @Column(nullable = false)
    private LocalDateTime bookingDate;

    @Column(nullable = false)
    private LocalDate serviceDate;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private String status; // PENDING, CONFIRMED, REJECTED, COMPLETED

    @Column(nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    private LocalDateTime updatedDate;
}
