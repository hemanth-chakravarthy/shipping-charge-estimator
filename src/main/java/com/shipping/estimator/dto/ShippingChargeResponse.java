package com.shipping.estimator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingChargeResponse {
    private double shippingCharge;
    private double baseCharge;
    private double fuelSurcharge;
    private double handlingFee;
    private double distanceKm;
    private double productSubtotal;
    private double grandTotal;
}
