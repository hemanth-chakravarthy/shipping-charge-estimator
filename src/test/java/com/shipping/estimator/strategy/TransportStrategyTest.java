package com.shipping.estimator.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransportStrategyTest {

    private TransportStrategyFactory factory;

    @BeforeEach
    void setUp() {
        factory = new TransportStrategyFactory(
                new MiniVanStrategy(),
                new TruckStrategy(),
                new AeroplaneStrategy());
    }

    // ── Strategy selection ──────────────────────────────────────────────────

    @Test
    void testDistance_50km_SelectsMiniVan() {
        TransportPricingStrategy strategy = factory.getStrategy(50);
        assertEquals("Mini Van", strategy.getModeName());
    }

    @Test
    void testDistance_99km_SelectsMiniVan() {
        TransportPricingStrategy strategy = factory.getStrategy(99);
        assertEquals("Mini Van", strategy.getModeName());
    }

    @Test
    void testDistance_100km_SelectsTruck() {
        TransportPricingStrategy strategy = factory.getStrategy(100);
        assertEquals("Truck", strategy.getModeName());
    }

    @Test
    void testDistance_300km_SelectsTruck() {
        TransportPricingStrategy strategy = factory.getStrategy(300);
        assertEquals("Truck", strategy.getModeName());
    }

    @Test
    void testDistance_499km_SelectsTruck() {
        TransportPricingStrategy strategy = factory.getStrategy(499);
        assertEquals("Truck", strategy.getModeName());
    }

    @Test
    void testDistance_500km_SelectsAeroplane() {
        TransportPricingStrategy strategy = factory.getStrategy(500);
        assertEquals("Aeroplane", strategy.getModeName());
    }

    @Test
    void testDistance_1000km_SelectsAeroplane() {
        TransportPricingStrategy strategy = factory.getStrategy(1000);
        assertEquals("Aeroplane", strategy.getModeName());
    }

    // ── Pricing logic ───────────────────────────────────────────────────────

    @Test
    void testMiniVanChargeCalculation() {
        // 50km × 3 Rs/km/kg × 10kg = 1500 Rs
        double charge = new MiniVanStrategy().calculate(50, 10);
        assertEquals(1500.0, charge, 0.01);
    }

    @Test
    void testTruckChargeCalculation() {
        // 200km × 2 Rs/km/kg × 5kg = 2000 Rs
        double charge = new TruckStrategy().calculate(200, 5);
        assertEquals(2000.0, charge, 0.01);
    }

    @Test
    void testAeroplaneChargeCalculation() {
        // 600km × 1 Rs/km/kg × 8kg = 4800 Rs
        double charge = new AeroplaneStrategy().calculate(600, 8);
        assertEquals(4800.0, charge, 0.01);
    }
}
