package com.homeservices.catalog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_offers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long serviceProviderId;

    @Column(nullable = false)
    private Long categoryId;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private LocalDate availableDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    private LocalDateTime updatedDate;
}
