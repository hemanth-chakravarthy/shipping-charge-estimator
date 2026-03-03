package com.shipping.estimator.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GeoDistanceUtilTest {

    @Test
    void testZeroDistance_SameCoordinates() {
        double distance = GeoDistanceUtil.calculateDistance(28.6139, 77.2090, 28.6139, 77.2090);
        assertEquals(0.0, distance, 0.001);
    }

    @Test
    void testKnownDistance_DelhiToMumbai() {
        // Approximate distance between Delhi and Mumbai is ~1150 km
        double distance = GeoDistanceUtil.calculateDistance(28.6139, 77.2090, 19.0760, 72.8777);
        assertTrue(distance > 1100 && distance < 1250,
                "Expected ~1150 km but got: " + distance);
    }

    @Test
    void testKnownDistance_ShortTrip() {
        // Two nearby coordinates should be < 100 km
        double distance = GeoDistanceUtil.calculateDistance(28.6139, 77.2090, 28.7041, 77.1025);
        assertTrue(distance < 20, "Expected < 20 km but got: " + distance);
    }

    @Test
    void testPrecision_FourDecimalPlaces() {
        double distance = GeoDistanceUtil.calculateDistance(12.9716, 77.5946, 13.0827, 80.2707);
        String distStr = String.valueOf(distance);
        int dotIndex = distStr.indexOf('.');
        if (dotIndex >= 0) {
            assertTrue(distStr.length() - dotIndex - 1 <= 4, "More than 4 decimal places");
        }
    }

    @Test
    void testDistance_IsPositive() {
        double distance = GeoDistanceUtil.calculateDistance(19.0760, 72.8777, 13.0827, 80.2707);
        assertTrue(distance > 0, "Distance should be positive");
    }
}
