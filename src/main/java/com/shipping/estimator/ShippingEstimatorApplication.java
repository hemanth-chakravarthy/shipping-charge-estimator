package com.shipping.estimator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ShippingEstimatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShippingEstimatorApplication.class, args);
    }
}
