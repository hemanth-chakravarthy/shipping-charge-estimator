package com.shipping.estimator.service;

import com.shipping.estimator.entity.Customer;
import com.shipping.estimator.entity.Product;
import com.shipping.estimator.entity.Warehouse;
import com.shipping.estimator.repository.CustomerRepository;
import com.shipping.estimator.repository.ProductRepository;
import com.shipping.estimator.repository.WarehouseRepository;
import com.shipping.estimator.util.GeoDistanceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShippingServiceV2 {

    private final WarehouseRepository warehouseRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public double calculateAdvancedShippingCharge(UUID warehouseId, UUID customerId, String deliverySpeed) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        double distance = GeoDistanceUtil.calculateDistance(
                warehouse.getLatitude(), warehouse.getLongitude(),
                customer.getLatitude(), customer.getLongitude()
        );

        // Calculate total weight (actual vs volumetric) for all products from this warehouse?
        // For simplicity in this demo, let's assume we are calculating for a "Standard Parcel" 
        // based on the first product associated with the warehouse or a default.
        // In a real scenario, this would be based on the actual items in the cart.
        
        // Let's assume a default billable weight for the calculation if no product is specified,
        // but for V2 we will use a "Billable Weight" concept.
        double billableWeight = 10.0; // Default 10kg for generic estimation
        
        return computePrice(distance, billableWeight, deliverySpeed);
    }

    public com.shipping.estimator.dto.ShippingChargeResponse calculateBulkShippingCharge(UUID warehouseId, UUID customerId, Map<UUID, Integer> items, String deliverySpeed) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId).orElseThrow();
        Customer customer = customerRepository.findById(customerId).orElseThrow();

        double totalActualWeight = 0;
        double totalVolumetricWeight = 0;
        double productSubtotal = 0;

        for (Map.Entry<UUID, Integer> entry : items.entrySet()) {
            Product product = productRepository.findById(entry.getKey()).orElseThrow();
            int quantity = entry.getValue();

            totalActualWeight += product.getWeightKg() * quantity;
            double unitVolumetric = (product.getLengthCm() * product.getWidthCm() * product.getHeightCm()) / 5000.0;
            totalVolumetricWeight += unitVolumetric * quantity;
            productSubtotal += product.getPrice() * quantity;
        }

        double billableWeight = Math.max(totalActualWeight, totalVolumetricWeight);
        double distance = GeoDistanceUtil.calculateDistance(warehouse.getLatitude(), warehouse.getLongitude(),
                customer.getLatitude(), customer.getLongitude());

        double ratePerKg = getTieredRate(distance);
        double baseCharge = billableWeight * ratePerKg;

        if ("express".equalsIgnoreCase(deliverySpeed)) {
            baseCharge *= 1.5;
        }

        double fuelSurcharge = Math.round((baseCharge * 0.05) * 100.0) / 100.0;
        double handlingFee = 100.0;
        double totalShipping = Math.round((baseCharge + fuelSurcharge + handlingFee) * 100.0) / 100.0;

        return com.shipping.estimator.dto.ShippingChargeResponse.builder()
                .shippingCharge(totalShipping)
                .baseCharge(Math.round(baseCharge * 100.0) / 100.0)
                .fuelSurcharge(fuelSurcharge)
                .handlingFee(handlingFee)
                .distanceKm(Math.round(distance * 100.0) / 100.0)
                .productSubtotal(Math.round(productSubtotal * 100.0) / 100.0)
                .grandTotal(Math.round((totalShipping + productSubtotal) * 100.0) / 100.0)
                .build();
    }

    private double getTieredRate(double distance) {
        if (distance <= 200) return 10.0;
        if (distance <= 500) return 8.0;
        if (distance <= 1000) return 6.0;
        return 5.0;
    }

    public com.shipping.estimator.dto.ShippingChargeResponse calculateProductShippingCharge(UUID warehouseId, UUID customerId, UUID productId, String deliverySpeed) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId).orElseThrow();
        Customer customer = customerRepository.findById(customerId).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();

        double distance = GeoDistanceUtil.calculateDistance(warehouse.getLatitude(), warehouse.getLongitude(),
                customer.getLatitude(), customer.getLongitude());

        double volumetricWeight = (product.getLengthCm() * product.getWidthCm() * product.getHeightCm()) / 5000.0;
        double billableWeight = Math.max(product.getWeightKg(), volumetricWeight);

        double ratePerKg = getTieredRate(distance);
        double baseCharge = billableWeight * ratePerKg;

        if ("express".equalsIgnoreCase(deliverySpeed)) {
            baseCharge *= 1.5;
        }

        double fuelSurcharge = Math.round((baseCharge * 0.05) * 100.0) / 100.0;
        double handlingFee = 100.0;
        double totalShipping = Math.round((baseCharge + fuelSurcharge + handlingFee) * 100.0) / 100.0;
        double productSubtotal = product.getPrice();

        return com.shipping.estimator.dto.ShippingChargeResponse.builder()
                .shippingCharge(totalShipping)
                .baseCharge(Math.round(baseCharge * 100.0) / 100.0)
                .fuelSurcharge(fuelSurcharge)
                .handlingFee(handlingFee)
                .distanceKm(Math.round(distance * 100.0) / 100.0)
                .productSubtotal(Math.round(productSubtotal * 100.0) / 100.0)
                .grandTotal(Math.round((totalShipping + productSubtotal) * 100.0) / 100.0)
                .build();
    }

    private double computePrice(double distance, double weight, String deliverySpeed) {
        // 3. Tiered Distance Pricing
        double ratePerKg;
        if (distance <= 200) {
            ratePerKg = 10.0;
        } else if (distance <= 500) {
            ratePerKg = 8.0;
        } else if (distance <= 1000) {
            ratePerKg = 6.0;
        } else {
            ratePerKg = 5.0;
        }

        double baseCost = distance * ratePerKg * (weight / 5.0); // Normalized per 5kg unit
        
        // 4. Base Fee
        double total = baseCost + 50.0;

        // 5. Fuel Surcharge (5%)
        total += total * 0.05;

        // 6. Express Surcharge (20%)
        if ("express".equalsIgnoreCase(deliverySpeed)) {
            total += total * 0.20;
        }

        return Math.round(total * 100.0) / 100.0; // Round to 2 decimal places
    }
}
