package com.shipping.estimator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CombinedShippingResponse {
    private double shippingCharge;
    private NearestWarehouseResponse nearestWarehouse;
}
