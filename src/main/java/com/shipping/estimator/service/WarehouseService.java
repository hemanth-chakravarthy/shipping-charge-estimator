package com.shipping.estimator.service;

import com.shipping.estimator.dto.NearestWarehouseResponse;
import com.shipping.estimator.dto.WarehouseLocation;
import com.shipping.estimator.entity.Product;
import com.shipping.estimator.entity.Seller;
import com.shipping.estimator.entity.Warehouse;
import com.shipping.estimator.exception.ResourceNotFoundException;
import com.shipping.estimator.repository.ProductRepository;
import com.shipping.estimator.repository.SellerRepository;
import com.shipping.estimator.repository.WarehouseRepository;
import com.shipping.estimator.util.GeoDistanceUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WarehouseService {

    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    public WarehouseService(SellerRepository sellerRepository,
            ProductRepository productRepository,
            WarehouseRepository warehouseRepository) {
        this.sellerRepository = sellerRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
    }

    @Cacheable(value = "nearestWarehouse", key = "#sellerId.toString()")
    public NearestWarehouseResponse getNearestWarehouse(UUID sellerId, UUID productId) {
        // Validate seller exists
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with id: " + sellerId));

        // Validate product exists and belongs to seller
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (!product.getSeller().getId().equals(sellerId)) {
            throw new IllegalArgumentException("Product does not belong to the specified seller");
        }

        // Find all warehouses
        List<Warehouse> warehouses = warehouseRepository.findAll();
        if (warehouses.isEmpty()) {
            throw new ResourceNotFoundException("No warehouses available in the system");
        }

        // Find nearest warehouse using Haversine distance
        Warehouse nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Warehouse warehouse : warehouses) {
            double dist = GeoDistanceUtil.calculateDistance(
                    seller.getLatitude(), seller.getLongitude(),
                    warehouse.getLatitude(), warehouse.getLongitude());
            if (dist < minDistance || (dist == minDistance && nearest != null
                    && warehouse.getId().compareTo(nearest.getId()) < 0)) {
                minDistance = dist;
                nearest = warehouse;
            }
        }

        return NearestWarehouseResponse.builder()
                .warehouseId(nearest.getId())
                .warehouseLocation(WarehouseLocation.builder()
                        .lat(nearest.getLatitude())
                        .lon(nearest.getLongitude())
                        .build())
                .build();
    }
}
