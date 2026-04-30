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
    public ResponseEntity<com.shipping.estimator.dto.MasterDataResponse> getAllData() {
        return ResponseEntity.ok(com.shipping.estimator.dto.MasterDataResponse.builder()
                .sellers(sellerRepository.findAll().stream().map(s -> com.shipping.estimator.dto.MasterDataResponse.SellerDTO.builder()
                        .id(s.getId()).name(s.getName()).latitude(s.getLatitude()).longitude(s.getLongitude()).build()).toList())
                .customers(customerRepository.findAll().stream().map(c -> com.shipping.estimator.dto.MasterDataResponse.CustomerDTO.builder()
                        .id(c.getId()).name(c.getName()).phoneNumber(c.getPhoneNumber()).latitude(c.getLatitude()).longitude(c.getLongitude()).build()).toList())
                .warehouses(warehouseRepository.findAll().stream().map(w -> com.shipping.estimator.dto.MasterDataResponse.WarehouseDTO.builder()
                        .id(w.getId()).name(w.getName()).latitude(w.getLatitude()).longitude(w.getLongitude()).build()).toList())
                .products(productRepository.findAll().stream().map(p -> com.shipping.estimator.dto.MasterDataResponse.ProductDTO.builder()
                        .id(p.getId()).name(p.getName()).sellerId(p.getSeller().getId()).weightKg(p.getWeightKg())
                        .lengthCm(p.getLengthCm()).widthCm(p.getWidthCm()).heightCm(p.getHeightCm()).price(p.getPrice()).build()).toList())
                .build());
    }
}
