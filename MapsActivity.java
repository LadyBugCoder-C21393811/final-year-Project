package com.example.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private String source, destination;
    private LatLng sourceLatLng, destinationLatLng;
    private DatabaseReference databaseRef, routesDatabaseRef;
    private Marker userMarker;
    private List<LatLng> routePoints;
    private boolean userHasMovedMap = false; // testing

    private List<LatLng> actualRoute = new ArrayList<>();

    private DatabaseReference hazardMarkersRef;

    private boolean isMarkerModeEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        databaseRef = FirebaseDatabase.getInstance().getReference("Reviews");
        routesDatabaseRef = FirebaseDatabase.getInstance().getReference("Routes");

        hazardMarkersRef = FirebaseDatabase.getInstance().getReference("HazardMarkers");
        Button startNavigation = findViewById(R.id.start_navigation_button);
        startNavigation.setOnClickListener(v -> {
            Toast.makeText(this, "Navigation Started", Toast.LENGTH_SHORT).show();

            if (sourceLatLng != null && destinationLatLng != null && routePoints != null) {
                saveRouteToFirebase(sourceLatLng, destinationLatLng, routePoints);
            }

            startLocationUpdates();
        });


        Intent intent = getIntent();
        if (intent != null) {
            source = intent.getStringExtra("source");
            destination = intent.getStringExtra("destination");
            sourceLatLng = intent.getParcelableExtra("sourceLatLng");
            destinationLatLng = intent.getParcelableExtra("destinationLatLng");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        Button toggleMarkerModeButton = findViewById(R.id.toggle_marker_mode_button);
        toggleMarkerModeButton.setOnClickListener(v -> {
            isMarkerModeEnabled = !isMarkerModeEnabled; // Toggle mode

            if (isMarkerModeEnabled) {
                toggleMarkerModeButton.setText("Disable Hazard Markers");
                Toast.makeText(this, "Tap on the map to add hazard markers", Toast.LENGTH_SHORT).show();
            } else {
                toggleMarkerModeButton.setText("Enable Hazard Markers");
            }
        });

    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    LatLng newLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    updateUserMarker(newLocation);

                    actualRoute.add(newLocation);

                    if (routePoints == null || !PolyUtil.isLocationOnPath(newLocation, routePoints, true, 50)) { // 50m tolerance
                        Log.d("MapsActivity", "User deviated from route, recalculating...");
                        fetchRouteFromGoogle(newLocation, destinationLatLng); // Fetch new route
                    }
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void updateUserMarker(LatLng location) {
        if (googleMap == null) return;

        if (userMarker == null) {
            userMarker = googleMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title("You are here"));
        } else {
            userMarker.setPosition(location);
        }

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(location));
    }

    private void updateMap(Location location) {
        if (googleMap != null) {
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

            if (userMarker != null) {
                userMarker.remove();
            }

            userMarker = googleMap.addMarker(new MarkerOptions().position(userLocation).title("You are here"));

            if (!userHasMovedMap) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
            }

            if (destinationLatLng != null) {
                Location destinationLocation = new Location("");
                destinationLocation.setLatitude(destinationLatLng.latitude);
                destinationLocation.setLongitude(destinationLatLng.longitude);

                if (location.distanceTo(destinationLocation) < 50) {
                    Log.d("MapsActivity", "User reached the destination!");

                    // ✅ Save actual traveled route when journey completes
                    saveActualRouteToFirebase();
                }
            }
        }
    }

    private void saveActualRouteToFirebase() {
        if (actualRoute.isEmpty()) return;

        String routeId = routesDatabaseRef.push().getKey();
        if (routeId == null) return;

        List<Map<String, Double>> completedPath = new ArrayList<>();
        for (LatLng point : actualRoute) {
            Map<String, Double> map = new HashMap<>();
            map.put("lat", point.latitude);
            map.put("lng", point.longitude);
            completedPath.add(map);
        }

        Map<String, Object> completedRouteData = new HashMap<>();
        completedRouteData.put("sourceLat", sourceLatLng.latitude);
        completedRouteData.put("sourceLng", sourceLatLng.longitude);
        completedRouteData.put("destinationLat", destinationLatLng.latitude);
        completedRouteData.put("destinationLng", destinationLatLng.longitude);
        completedRouteData.put("actualRoute", completedPath);
        completedRouteData.put("completedAt", System.currentTimeMillis());

        routesDatabaseRef.child(routeId).setValue(completedRouteData)
                .addOnSuccessListener(aVoid -> Log.d("MapsActivity", "Actual route saved!"))
                .addOnFailureListener(e -> Log.e("MapsActivity", "Failed to save actual route", e));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.getUiSettings().setZoomControlsEnabled(true);

        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            getLocation();
            startLocationUpdates();
        } else {
            checkLocationPermission();
        }

        new android.os.Handler().postDelayed(() -> {
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }, 2000);

        googleMap.setOnCameraMoveStartedListener(reason -> {
            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                userHasMovedMap = true; // User has manually moved the map
            }
        });

        googleMap.setOnMapClickListener(latLng -> {
            if (isMarkerModeEnabled) {
                addHazardMarker(latLng);
            }
        });

        googleMap.setOnCameraMoveListener(() -> {
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        });

        if (sourceLatLng != null && destinationLatLng != null) {
            googleMap.setOnMapLoadedCallback(() -> {
                getRouteFromAPI(sourceLatLng, destinationLatLng);
            });
        } else {
            Toast.makeText(this, "No route available. Please select locations.", Toast.LENGTH_SHORT).show();
        }
    }

    private void addHazardMarker(LatLng latLng) {
        String markerId = hazardMarkersRef.push().getKey(); // Generate unique ID

        if (markerId == null) return;

        Map<String, Object> markerData = new HashMap<>();
        markerData.put("lat", latLng.latitude);
        markerData.put("lng", latLng.longitude);
        markerData.put("timestamp", System.currentTimeMillis());

        hazardMarkersRef.child(markerId).setValue(markerData)
                .addOnSuccessListener(aVoid -> {
                    googleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title("Hazard Marker"));
                    Log.d("MapsActivity", "Hazard marker added at: " + latLng);
                })
                .addOnFailureListener(e -> Log.e("MapsActivity", "Failed to save hazard marker", e));
    }

    private void loadHazardMarkers() {
        hazardMarkersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot markerSnapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> markerData = (Map<String, Object>) markerSnapshot.getValue();
                    if (markerData != null) {
                        double lat = (double) markerData.get("lat");
                        double lng = (double) markerData.get("lng");

                        LatLng hazardLocation = new LatLng(lat, lng);
                        googleMap.addMarker(new MarkerOptions()
                                .position(hazardLocation)
                                .title("Reported Hazard"));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MapsActivity", "Error loading hazard markers", databaseError.toException());
            }
        });
    }

    private void getRouteFromAPI(LatLng source, LatLng destination) {
        routesDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean routeExists = false;

                for (DataSnapshot routeSnapshot : dataSnapshot.getChildren()) {
                    Route existingRoute = routeSnapshot.getValue(Route.class);

                    if (existingRoute != null) {
                        LatLng existingSource = existingRoute.getSource();
                        LatLng existingDestination = existingRoute.getDestination();

                        if (existingSource.equals(source) && existingDestination.equals(destination)) {
                            Log.d("MapsActivity", "Existing route found in Firebase!");
                            drawRouteOnMap(existingRoute.getRoutePoints());
                            routeExists = true;
                            break;
                        }
                    }
                }

                if (!routeExists) {
                    fetchRouteFromGoogle(source, destination);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MapsActivity", "Error checking Firebase for existing route", databaseError.toException());
            }
        });
    }

    private void fetchRouteFromGoogle(LatLng source, LatLng destination) {
        String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + source.latitude + "," + source.longitude +
                "&destination=" + destination.latitude + "," + destination.longitude +
                "&mode=driving&key=AIzaSyAXHl5VIPZF0uP0G-wbpxJ0AAWSl5m5_SU";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray routes = response.getJSONArray("routes");
                        if (routes.length() > 0) {
                            JSONObject route = routes.getJSONObject(0);
                            JSONObject polyline = route.getJSONObject("overview_polyline");
                            String points = polyline.getString("points");

                            routePoints = PolyUtil.decode(points);
                            drawRouteOnMap(routePoints);
                            saveRouteToFirebase(source, destination, routePoints);
                        } else {
                            Log.e("MapsActivity", "No routes found from Google API.");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("MapsActivity", "Error fetching route from Google API", error));

        requestQueue.add(request);
    }

    private void saveRouteToFirebase(LatLng source, LatLng destination, List<LatLng> routePoints) {
        String routeId = routesDatabaseRef.push().getKey();
        if (routeId == null) return;

        List<Map<String, Double>> routePath = new ArrayList<>();
        for (LatLng point : routePoints) {
            Map<String, Double> map = new HashMap<>();
            map.put("lat", point.latitude);
            map.put("lng", point.longitude);
            routePath.add(map);
        }

        // 🔹 Store route data in Firebase
        Map<String, Object> routeData = new HashMap<>();
        routeData.put("sourceLat", source.latitude);
        routeData.put("sourceLng", source.longitude);
        routeData.put("destinationLat", destination.latitude);
        routeData.put("destinationLng", destination.longitude);
        routeData.put("routePoints", routePath);
        routeData.put("createdAt", System.currentTimeMillis());

        routesDatabaseRef.child(routeId).setValue(routeData)
                .addOnSuccessListener(aVoid -> Log.d("MapsActivity", "Route saved successfully!"))
                .addOnFailureListener(e -> Log.e("MapsActivity", "Failed to save route", e));
    }

    private void drawRouteOnMap(List<LatLng> routePoints) {
        if (googleMap == null || routePoints.isEmpty()) return;

        googleMap.clear();
        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(routePoints)
                .width(12)
                .color(Color.RED)
                .geodesic(true);

        googleMap.addPolyline(polylineOptions);
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    updateMap(location);
                }
            });
        }
    }
}