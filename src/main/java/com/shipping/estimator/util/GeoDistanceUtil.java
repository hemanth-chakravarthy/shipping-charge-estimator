package com.shipping.estimator.util;

public class GeoDistanceUtil {

    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Calculates the great-circle distance between two coordinates using the
     * Haversine formula.
     *
     * @return distance in kilometres, rounded to 4 decimal places
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distanceKm = EARTH_RADIUS_KM * c;

        // Round to 4 decimal places
        return Math.round(distanceKm * 10000.0) / 10000.0;
    }
}
