package com.shipping.estimator.controller;

import com.shipping.estimator.dto.ShippingChargeResponse;
import com.shipping.estimator.service.ShippingServiceV2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v2/shipping-charge")
@RequiredArgsConstructor
@Tag(name = "Shipping Estimator V2", description = "Advanced Logistics Engine with Volumetric Weight & Tiered Pricing")
public class ShippingControllerV2 {

    private final ShippingServiceV2 shippingServiceV2;

    private final com.shipping.estimator.service.InvoiceService invoiceService;
    private final com.shipping.estimator.repository.WarehouseRepository warehouseRepository;
    private final com.shipping.estimator.repository.CustomerRepository customerRepository;
    private final com.shipping.estimator.repository.ProductRepository productRepository;

    @GetMapping
    @Operation(summary = "Calculate advanced shipping charge for a specific product")
    public ResponseEntity<com.shipping.estimator.dto.ShippingChargeResponse> getAdvancedShippingCharge(
            @RequestParam java.util.UUID warehouseId,
            @RequestParam java.util.UUID customerId,
            @RequestParam java.util.UUID productId,
            @RequestParam(defaultValue = "standard") String deliverySpeed) {

        return ResponseEntity.ok(shippingServiceV2.calculateProductShippingCharge(warehouseId, customerId, productId, deliverySpeed));
    }

    @PostMapping("/bulk")
    @Operation(summary = "Calculate shipping charge for multiple items with quantities")
    public ResponseEntity<com.shipping.estimator.dto.ShippingChargeResponse> getBulkShippingCharge(
            @RequestParam java.util.UUID warehouseId,
            @RequestParam java.util.UUID customerId,
            @RequestBody java.util.Map<java.util.UUID, Integer> items,
            @RequestParam(defaultValue = "standard") String deliverySpeed) {

        return ResponseEntity.ok(shippingServiceV2.calculateBulkShippingCharge(warehouseId, customerId, items, deliverySpeed));
    }

    @PostMapping("/pdf-invoice-bulk")
    @Operation(summary = "Generate a PDF invoice for a bulk shipping quote")
    public ResponseEntity<byte[]> downloadBulkInvoice(
            @RequestParam java.util.UUID warehouseId,
            @RequestParam java.util.UUID customerId,
            @RequestBody java.util.Map<java.util.UUID, Integer> items,
            @RequestParam(defaultValue = "standard") String deliverySpeed) {

        com.shipping.estimator.dto.ShippingChargeResponse response = shippingServiceV2.calculateBulkShippingCharge(warehouseId, customerId, items, deliverySpeed);
        
        com.shipping.estimator.entity.Customer customer = customerRepository.findById(customerId).orElseThrow();
        com.shipping.estimator.entity.Warehouse warehouse = warehouseRepository.findById(warehouseId).orElseThrow();
        
        java.util.Map<String, Integer> itemNames = new java.util.HashMap<>();
        for (java.util.Map.Entry<java.util.UUID, Integer> entry : items.entrySet()) {
            String name = productRepository.findById(entry.getKey()).orElseThrow().getName();
            itemNames.put(name, entry.getValue());
        }

        java.io.ByteArrayInputStream bis = invoiceService.generateBulkInvoice(customer.getName(), warehouse.getName(), itemNames, response);

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=bulk_invoice.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(bis.readAllBytes());
    }

    @GetMapping("/pdf-invoice")
    @Operation(summary = "Generate a PDF invoice for a shipping quote")
    public ResponseEntity<byte[]> downloadInvoice(
            @RequestParam java.util.UUID warehouseId,
            @RequestParam java.util.UUID customerId,
            @RequestParam java.util.UUID productId,
            @RequestParam(defaultValue = "standard") String deliverySpeed) {

        com.shipping.estimator.dto.ShippingChargeResponse response = shippingServiceV2.calculateProductShippingCharge(warehouseId, customerId, productId, deliverySpeed);
        
        com.shipping.estimator.entity.Customer customer = customerRepository.findById(customerId).orElseThrow();
        com.shipping.estimator.entity.Warehouse warehouse = warehouseRepository.findById(warehouseId).orElseThrow();
        com.shipping.estimator.entity.Product product = productRepository.findById(productId).orElseThrow();

        java.util.Map<String, Integer> items = new java.util.HashMap<>();
        items.put(product.getName(), 1);

        java.io.ByteArrayInputStream bis = invoiceService.generateBulkInvoice(customer.getName(), warehouse.getName(), items, response);

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=shipping-quote.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(bis.readAllBytes());
    }
}
