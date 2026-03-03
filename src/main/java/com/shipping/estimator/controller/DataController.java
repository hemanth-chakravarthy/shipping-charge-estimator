package com.shipping.estimator.controller;

import com.shipping.estimator.entity.Customer;
import com.shipping.estimator.entity.Product;
import com.shipping.estimator.entity.Seller;
import com.shipping.estimator.entity.Warehouse;
import com.shipping.estimator.repository.CustomerRepository;
import com.shipping.estimator.repository.ProductRepository;
import com.shipping.estimator.repository.SellerRepository;
import com.shipping.estimator.repository.WarehouseRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/data")
@Tag(name = "Demo Data", description = "Retrieve seeded sample data IDs for testing")
public class DataController {

    private final SellerRepository sellerRepository;
    private final CustomerRepository customerRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;

    public DataController(SellerRepository sellerRepository,
            CustomerRepository customerRepository,
            WarehouseRepository warehouseRepository,
            ProductRepository productRepository) {
        this.sellerRepository = sellerRepository;
        this.customerRepository = customerRepository;
        this.warehouseRepository = warehouseRepository;
        this.productRepository = productRepository;
    }

    @GetMapping
    @Operation(summary = "List all seeded sample data", description = "Returns all pre-loaded sellers, customers, warehouses, and products with their IDs. Use these IDs to test the shipping APIs.")
    public ResponseEntity<Map<String, Object>> getAllData() {
        List<Seller> sellers = sellerRepository.findAll();
        List<Customer> customers = customerRepository.findAll();
        List<Warehouse> warehouses = warehouseRepository.findAll();
        List<Product> products = productRepository.findAll();

        Map<String, Object> result = new HashMap<>();
        result.put("sellers", sellers.stream().map(s -> Map.of(
                "id", s.getId(),
                "name", s.getName(),
                "latitude", s.getLatitude(),
                "longitude", s.getLongitude())).toList());
        result.put("customers", customers.stream().map(c -> Map.of(
                "id", c.getId(),
                "name", c.getName(),
                "phoneNumber", c.getPhoneNumber(),
                "latitude", c.getLatitude(),
                "longitude", c.getLongitude())).toList());
        result.put("warehouses", warehouses.stream().map(w -> Map.of(
                "id", w.getId(),
                "name", w.getName(),
                "latitude", w.getLatitude(),
                "longitude", w.getLongitude())).toList());
        result.put("products", products.stream().map(p -> Map.of(
                "id", p.getId(),
                "name", p.getName(),
                "sellerId", p.getSeller().getId(),
                "weightKg", p.getWeightKg())).toList());

        return ResponseEntity.ok(result);
    }
}
