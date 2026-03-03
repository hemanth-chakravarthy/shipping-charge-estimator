package com.shipping.estimator.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CombinedShippingRequest {

    @NotNull(message = "sellerId is required")
    private UUID sellerId;

    @NotNull(message = "customerId is required")
    private UUID customerId;

    @NotNull(message = "deliverySpeed is required")
    @Pattern(regexp = "standard|express", message = "deliverySpeed must be 'standard' or 'express'")
    private String deliverySpeed;
}
