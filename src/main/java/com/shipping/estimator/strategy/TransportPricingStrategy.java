package com.shipping.estimator.strategy;

public interface TransportPricingStrategy {
    /**
     * Calculates base shipping charge (excluding standard fee and express
     * surcharge).
     *
     * @param distanceKm distance in kilometres
     * @param weightKg   product weight in kilograms
     * @return base charge in Rs
     */
    double calculate(double distanceKm, double weightKg);

    String getModeName();
}
