package com.shipping.estimator.strategy;

import org.springframework.stereotype.Component;

@Component
public class TransportStrategyFactory {

    private final MiniVanStrategy miniVanStrategy;
    private final TruckStrategy truckStrategy;
    private final AeroplaneStrategy aeroplaneStrategy;

    public TransportStrategyFactory(MiniVanStrategy miniVanStrategy,
            TruckStrategy truckStrategy,
            AeroplaneStrategy aeroplaneStrategy) {
        this.miniVanStrategy = miniVanStrategy;
        this.truckStrategy = truckStrategy;
        this.aeroplaneStrategy = aeroplaneStrategy;
    }

    /**
     * Selects the appropriate transport strategy based on distance.
     * 0–100 km → Mini Van (rate: Rs 3/km/kg)
     * 100–500 km → Truck (rate: Rs 2/km/kg)
     * 500+ km → Aeroplane(rate: Rs 1/km/kg)
     */
    public TransportPricingStrategy getStrategy(double distanceKm) {
        if (distanceKm < 100) {
            return miniVanStrategy;
        } else if (distanceKm < 500) {
            return truckStrategy;
        } else {
            return aeroplaneStrategy;
        }
    }
}
