package com.shipping.estimator.strategy;

import org.springframework.stereotype.Component;

@Component
public class AeroplaneStrategy implements TransportPricingStrategy {

    private static final double RATE_PER_KM_PER_KG = 1.0; // Rs 1 per km per kg

    @Override
    public double calculate(double distanceKm, double weightKg) {
        return distanceKm * RATE_PER_KM_PER_KG * weightKg;
    }

    @Override
    public String getModeName() {
        return "Aeroplane";
    }
}
