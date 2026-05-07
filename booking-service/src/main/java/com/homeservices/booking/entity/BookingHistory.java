package com.homeservices.booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long bookingId;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime statusChangedDate = LocalDateTime.now();
}
