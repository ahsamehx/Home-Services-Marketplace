package com.homeservices.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String transactionType; // BOOKING, REFUND, ADD_FUNDS

    @Column(nullable = false)
    private Long bookingId;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    private String status; // SUCCESS, FAILED
}
