package com.shipping.estimator.controller;

import com.shipping.estimator.dto.NearestWarehouseResponse;
import com.shipping.estimator.service.WarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/warehouse")
@Tag(name = "Warehouse", description = "Warehouse management APIs")
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @GetMapping("/nearest")
    @Operation(summary = "Get nearest warehouse for a seller", description = "Returns the nearest warehouse to the seller's location based on Haversine distance formula")
    public ResponseEntity<NearestWarehouseResponse> getNearestWarehouse(
            @RequestParam UUID sellerId,
            @RequestParam UUID productId) {
        return ResponseEntity.ok(warehouseService.getNearestWarehouse(sellerId, productId));
    }
}
