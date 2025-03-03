package com.example.fyp;

import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Route {
    private double sourceLat;
    private double sourceLng;
    private double destinationLat;
    private double destinationLng;
    private List<Map<String, Double>> routePoints;

    // Default constructor for Firebase
    public Route() {}

    public Route(LatLng source, LatLng destination, List<LatLng> routePoints) {
        this.sourceLat = source.latitude;
        this.sourceLng = source.longitude;
        this.destinationLat = destination.latitude;
        this.destinationLng = destination.longitude;

        this.routePoints = new ArrayList<>();
        for (LatLng point : routePoints) {
            Map<String, Double> map = new HashMap<>();
            map.put("lat", point.latitude);
            map.put("lng", point.longitude);
            this.routePoints.add(map);
        }
    }

    public LatLng getSource() {
        return new LatLng(sourceLat, sourceLng);
    }

    public LatLng getDestination() {
        return new LatLng(destinationLat, destinationLng);
    }

    public List<LatLng> getRoutePoints() {
        List<LatLng> points = new ArrayList<>();
        for (Map<String, Double> map : routePoints) {
            points.add(new LatLng(map.get("lat"), map.get("lng")));
        }
        return points;
}
}