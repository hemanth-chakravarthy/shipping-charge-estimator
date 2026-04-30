package com.shipping.estimator.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @Column(nullable = false)
    @jakarta.validation.constraints.NotBlank(message = "Product name is required")
    private String name;

    @Column(name = "weight_kg", nullable = false)
    @jakarta.validation.constraints.Positive(message = "Weight must be positive")
    private double weightKg;

    @Column(name = "length_cm")
    @jakarta.validation.constraints.Positive(message = "Length must be positive")
    private double lengthCm;

    @Column(name = "width_cm")
    @jakarta.validation.constraints.Positive(message = "Width must be positive")
    private double widthCm;

    @Column(name = "height_cm")
    @jakarta.validation.constraints.Positive(message = "Height must be positive")
    private double heightCm;

    @jakarta.validation.constraints.PositiveOrZero(message = "Price cannot be negative")
    private double price;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
