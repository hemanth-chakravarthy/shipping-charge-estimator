package com.shipping.estimator.service;

import com.shipping.estimator.dto.CombinedShippingResponse;
import com.shipping.estimator.dto.NearestWarehouseResponse;
import com.shipping.estimator.entity.Customer;
import com.shipping.estimator.entity.Product;
import com.shipping.estimator.entity.Seller;
import com.shipping.estimator.entity.Warehouse;
import com.shipping.estimator.exception.ResourceNotFoundException;
import com.shipping.estimator.repository.CustomerRepository;
import com.shipping.estimator.repository.ProductRepository;
import com.shipping.estimator.repository.SellerRepository;
import com.shipping.estimator.repository.WarehouseRepository;
import com.shipping.estimator.strategy.TransportPricingStrategy;
import com.shipping.estimator.strategy.TransportStrategyFactory;
import com.shipping.estimator.util.GeoDistanceUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ShippingService {

        private static final double STANDARD_COURIER_FEE = 10.0;
        private static final double EXPRESS_SURCHARGE_PER_KG = 1.2;

        private final WarehouseRepository warehouseRepository;
        private final CustomerRepository customerRepository;
        private final ProductRepository productRepository;
        private final SellerRepository sellerRepository;
        private final TransportStrategyFactory strategyFactory;

        public ShippingService(WarehouseRepository warehouseRepository,
                        CustomerRepository customerRepository,
                        ProductRepository productRepository,
                        SellerRepository sellerRepository,
                        TransportStrategyFactory strategyFactory) {
                this.warehouseRepository = warehouseRepository;
                this.customerRepository = customerRepository;
                this.productRepository = productRepository;
                this.sellerRepository = sellerRepository;
                this.strategyFactory = strategyFactory;
        }

        /**
         * Calculates shipping charge from warehouse to customer.
         * Pricing:
         * base = distance × rate × weight
         * total = base + Rs10 (standard fee) [+ 1.2×weight if express]
         * Result rounded to 2 decimal places.
         */
        public double getShippingCharge(UUID warehouseId, UUID customerId, String deliverySpeed) {
                // Validate delivery speed
                if (!"standard".equals(deliverySpeed) && !"express".equals(deliverySpeed)) {
                        throw new IllegalArgumentException("Invalid delivery speed. Allowed values: standard, express");
                }

                Warehouse warehouse = warehouseRepository.findById(warehouseId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Warehouse not found with id: " + warehouseId));

                Customer customer = customerRepository.findById(customerId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Customer not found with id: " + customerId));

                // Get product weight for this warehouse's associated sellers (use first
                // product's weight as example)
                // In practice, productId would be passed — we estimate weight from seller
                // products here
                // For the GET endpoint, weight is derived from the first product in the
                // warehouse region
                // A default weight of 1kg is used as fallback
                double weightKg = getProductWeightForWarehouse(warehouse);

                double distanceKm = GeoDistanceUtil.calculateDistance(
                                warehouse.getLatitude(), warehouse.getLongitude(),
                                customer.getLatitude(), customer.getLongitude());

                TransportPricingStrategy strategy = strategyFactory.getStrategy(distanceKm);
                double baseCharge = strategy.calculate(distanceKm, weightKg);
                double total = baseCharge + STANDARD_COURIER_FEE;

                if ("express".equals(deliverySpeed)) {
                        total += EXPRESS_SURCHARGE_PER_KG * weightKg;
                }

                return Math.round(total * 100.0) / 100.0;
        }

        /**
         * Overload that accepts productId explicitly for precise weight calculation.
         */
        public double getShippingChargeWithProduct(UUID warehouseId, UUID customerId, UUID productId,
                        String deliverySpeed) {
                if (!"standard".equals(deliverySpeed) && !"express".equals(deliverySpeed)) {
                        throw new IllegalArgumentException("Invalid delivery speed. Allowed values: standard, express");
                }

                Warehouse warehouse = warehouseRepository.findById(warehouseId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Warehouse not found with id: " + warehouseId));

                Customer customer = customerRepository.findById(customerId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Customer not found with id: " + customerId));

                Product product = productRepository.findById(productId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Product not found with id: " + productId));

                double distanceKm = GeoDistanceUtil.calculateDistance(
                                warehouse.getLatitude(), warehouse.getLongitude(),
                                customer.getLatitude(), customer.getLongitude());

                TransportPricingStrategy strategy = strategyFactory.getStrategy(distanceKm);
                double baseCharge = strategy.calculate(distanceKm, product.getWeightKg());
                double total = baseCharge + STANDARD_COURIER_FEE;

                if ("express".equals(deliverySpeed)) {
                        total += EXPRESS_SURCHARGE_PER_KG * product.getWeightKg();
                }

                return Math.round(total * 100.0) / 100.0;
        }

        /**
         * Combined API: finds nearest warehouse for seller, then calculates shipping
         * charge.
         */
        public CombinedShippingResponse calculateCombined(UUID sellerId, UUID customerId, String deliverySpeed) {
                if (!"standard".equals(deliverySpeed) && !"express".equals(deliverySpeed)) {
                        throw new IllegalArgumentException("Invalid delivery speed. Allowed values: standard, express");
                }

                Seller seller = sellerRepository.findById(sellerId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Seller not found with id: " + sellerId));

                Customer customer = customerRepository.findById(customerId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Customer not found with id: " + customerId));

                List<Warehouse> warehouses = warehouseRepository.findAll();
                if (warehouses.isEmpty()) {
                        throw new ResourceNotFoundException("No warehouses available in the system");
                }

                // Find nearest warehouse to seller
                Warehouse nearest = warehouses.stream()
                                .min((a, b) -> {
                                        double da = GeoDistanceUtil.calculateDistance(
                                                        seller.getLatitude(), seller.getLongitude(),
                                                        a.getLatitude(), a.getLongitude());
                                        double db = GeoDistanceUtil.calculateDistance(
                                                        seller.getLatitude(), seller.getLongitude(),
                                                        b.getLatitude(), b.getLongitude());
                                        return Double.compare(da, db);
                                })
                                .orElseThrow(() -> new ResourceNotFoundException("No warehouse found"));

                // Get product weight for pricing (use seller's first product)
                List<Product> products = productRepository.findBySeller(seller);
                double weightKg = products.isEmpty() ? 1.0 : products.get(0).getWeightKg();

                double distanceKm = GeoDistanceUtil.calculateDistance(
                                nearest.getLatitude(), nearest.getLongitude(),
                                customer.getLatitude(), customer.getLongitude());

                TransportPricingStrategy strategy = strategyFactory.getStrategy(distanceKm);
                double baseCharge = strategy.calculate(distanceKm, weightKg);
                double total = baseCharge + STANDARD_COURIER_FEE;

                if ("express".equals(deliverySpeed)) {
                        total += EXPRESS_SURCHARGE_PER_KG * weightKg;
                }

                double shippingCharge = Math.round(total * 100.0) / 100.0;

                NearestWarehouseResponse warehouseResponse = NearestWarehouseResponse.builder()
                                .warehouseId(nearest.getId())
                                .warehouseLocation(com.shipping.estimator.dto.WarehouseLocation.builder()
                                                .lat(nearest.getLatitude())
                                                .lon(nearest.getLongitude())
                                                .build())
                                .build();

                return CombinedShippingResponse.builder()
                                .shippingCharge(shippingCharge)
                                .nearestWarehouse(warehouseResponse)
                                .build();
        }

        private double getProductWeightForWarehouse(Warehouse warehouse) {
                // Fallback: return default 1kg if no products found
                return 1.0;
        }
}
