package com.github.pksokolowski.smogmid.utils;

public final class DistanceHelper {
    private DistanceHelper() {
    }

    public static double distance(LatLng A, LatLng B) {
        // prepare parameters
        final var lat1 = A.getLatitude();
        final var lon1 = A.getLongitude();
        final var lat2 = B.getLatitude();
        final var lon2 = B.getLongitude();

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        // in KM
        dist = dist * 1.609344;

        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}
