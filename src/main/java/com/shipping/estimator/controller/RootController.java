package com.shipping.estimator.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RootController {

    @Hidden
    @GetMapping("/")
    public ResponseEntity<Map<String, String>> root() {
        return ResponseEntity.ok(info());
    }

    @Hidden
    @GetMapping({"/api/v1", "/api/v2"})
    public ResponseEntity<Map<String, String>> apiRoot() {
        return ResponseEntity.ok(info());
    }

    private Map<String, String> info() {
        return Map.of(
            "application", "B2B Shipping Charge Estimator",
            "version", "2.0.0 (Advanced Engine)",
            "swagger", "http://localhost:8082/swagger-ui.html",
            "v1_data", "http://localhost:8082/api/v1/data",
            "v2_shipping", "http://localhost:8082/api/v2/shipping-charge"
        );
    }
}
