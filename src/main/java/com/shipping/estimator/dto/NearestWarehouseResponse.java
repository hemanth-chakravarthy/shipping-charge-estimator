package com.shipping.estimator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NearestWarehouseResponse {
    private UUID warehouseId;
    private WarehouseLocation warehouseLocation;
}
