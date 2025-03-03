package com.example.fyp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.model.LatLng;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;


import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class SearchLocationActivity extends AppCompatActivity {

    private static final int AUTOCOMPLETE_REQUEST_CODE_SOURCE = 1;
    private static final int AUTOCOMPLETE_REQUEST_CODE_DESTINATION = 2;

    private EditText sourceInput, destinationInput;
    private Button btnSubmit, btnCurrentLocation;
    private LatLng sourceLatLng, destinationLatLng;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);

        // Initialize Places API
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyAXHl5VIPZF0uP0G-wbpxJ0AAWSl5m5_SU");
        }

        sourceInput = findViewById(R.id.source_input);
        destinationInput = findViewById(R.id.destination_input);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnCurrentLocation = findViewById(R.id.btnCurrentLocation);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Open Google Places Autocomplete
        sourceInput.setFocusable(false);
        sourceInput.setOnClickListener(v -> openAutocomplete(AUTOCOMPLETE_REQUEST_CODE_SOURCE));

        destinationInput.setFocusable(false);
        destinationInput.setOnClickListener(v -> openAutocomplete(AUTOCOMPLETE_REQUEST_CODE_DESTINATION));

        btnSubmit.setOnClickListener(v -> {
            if (sourceLatLng == null || destinationLatLng == null) {
                Toast.makeText(this, "Please select both locations", Toast.LENGTH_SHORT).show();
                return;
            }

            // Send data to MapsActivity
            Intent intent = new Intent(SearchLocationActivity.this, MapsActivity.class);
            intent.putExtra("source", sourceInput.getText().toString());
            intent.putExtra("destination", destinationInput.getText().toString());
            intent.putExtra("sourceLatLng", sourceLatLng);
            intent.putExtra("destinationLatLng", destinationLatLng);

            // Start MapsActivity
            startActivity(intent);

            // Finish SearchLocationActivity
            finish();
        });

        btnCurrentLocation.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                Log.d("searchActivity", "come here 99 ");

                // Request Location Permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                getCurrentLocation();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                sourceLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                Log.d("searchActivity", "Latitude: " + location.getLatitude());
                Log.d("searchActivity", "Longitude: " + location.getLongitude());

                // Call method to fetch address
                getAddressFromLatLng(location.getLatitude(), location.getLongitude());

            } else {
                Toast.makeText(this, "Unable to fetch location", Toast.LENGTH_SHORT).show();
                Log.e("searchActivity", "Location is null");
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error fetching location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("searchActivity", "Error: " + e.getMessage());
        });
    }

    private void getAddressFromLatLng(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String fullAddress = address.getAddressLine(0);

                Log.d("searchActivity", "Address: " + fullAddress);

                // Update the UI on the main thread
                runOnUiThread(() -> {
                    sourceInput.setText(fullAddress); // Set fetched address in source input field
                });
            } else {
                Toast.makeText(this, "Address not found!", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error fetching address: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    // Open Google Places Autocomplete
    private void openAutocomplete(int requestCode) {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startActivityForResult(intent, requestCode);
    }

    // Handle Autocomplete result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Place place = Autocomplete.getPlaceFromIntent(data);

            if (requestCode == AUTOCOMPLETE_REQUEST_CODE_SOURCE) {
                sourceInput.setText(place.getName());
                sourceLatLng = place.getLatLng(); // Store LatLng for later use
            } else if (requestCode == AUTOCOMPLETE_REQUEST_CODE_DESTINATION) {
                destinationInput.setText(place.getName());
                destinationLatLng = place.getLatLng();
            }
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Log.e("SearchLocationActivity", "Error: " + status.getStatusMessage());
        }
    }
}
