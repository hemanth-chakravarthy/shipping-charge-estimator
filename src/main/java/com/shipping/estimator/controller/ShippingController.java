package com.shipping.estimator.controller;

import com.shipping.estimator.dto.CombinedShippingRequest;
import com.shipping.estimator.dto.CombinedShippingResponse;
import com.shipping.estimator.dto.ShippingChargeResponse;
import com.shipping.estimator.service.ShippingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shipping-charge")
@Tag(name = "Shipping", description = "Shipping charge calculation APIs")
public class ShippingController {

    private final ShippingService shippingService;

    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @GetMapping
    @Operation(summary = "Get shipping charge (warehouse → customer)", description = "Calculates shipping charge based on distance, weight, transport mode, and delivery speed")
    public ResponseEntity<ShippingChargeResponse> getShippingCharge(
            @RequestParam UUID warehouseId,
            @RequestParam UUID customerId,
            @RequestParam String deliverySpeed) {
        double charge = shippingService.getShippingCharge(warehouseId, customerId, deliverySpeed);
        return ResponseEntity.ok(ShippingChargeResponse.builder().shippingCharge(charge).build());
    }

    @PostMapping("/calculate")
    @Operation(summary = "Combined shipping calculation", description = "Finds nearest warehouse for seller then calculates end-to-end shipping charge in one request")
    public ResponseEntity<CombinedShippingResponse> calculateCombined(
            @RequestBody @Valid CombinedShippingRequest request) {
        CombinedShippingResponse response = shippingService.calculateCombined(
                request.getSellerId(),
                request.getCustomerId(),
                request.getDeliverySpeed());
        return ResponseEntity.ok(response);
    }
}
