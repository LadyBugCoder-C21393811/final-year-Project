package com.example.fyp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.gms.common.api.Status;
import java.util.Arrays;
import java.util.List;



public class LeaveReviewActivity extends AppCompatActivity {

    // UI elements
    private TextView boxGreen, boxOrange, boxRed;
    private EditText etPlaceName, etReview;
    private Button btnSubmitReview;
    public FirebaseUser currentUser;


    private String selectedColor = "#A9A9A9"; // Default color (gray)

    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_leave_review);

        // Apply edge-to-edge insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Initialize Places if not already done
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyAXHl5VIPZF0uP0G-wbpxJ0AAWSl5m5_SU");
        }

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("Reviews");
        currentUser = mAuth.getCurrentUser();
        Log.d("FirebaseAuth", "User UID: " + currentUser.getUid());


        // Initialize UI elements
        boxGreen = findViewById(R.id.boxGreen);
        boxOrange = findViewById(R.id.boxOrange);
        boxRed = findViewById(R.id.boxRed);
        etPlaceName = findViewById(R.id.etPlaceName);
        etReview = findViewById(R.id.etReview);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);


        // Prevent manual text input; only allow selection from autocomplete
        etPlaceName.setFocusable(false);
        etPlaceName.setOnClickListener(v -> {
            // Define the fields to return after the user has made a selection.
            List<Place.Field> fields = Arrays.asList(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.ADDRESS,
                    Place.Field.LAT_LNG
            );
            // Create the autocomplete intent with fullscreen mode.
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(LeaveReviewActivity.this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });


        // Set up click listeners for the color boxes
        boxGreen.setOnClickListener(v -> {
            resetColors(); // Reset all boxes to white
            boxGreen.setBackgroundColor(Color.GREEN); // Highlight green box
            selectedColor = "#00FF00"; // Save green color code
        });

        boxOrange.setOnClickListener(v -> {
            resetColors(); // Reset all boxes to white
            boxOrange.setBackgroundColor(Color.parseColor("#FFA500")); // Highlight orange box
            selectedColor = "#FFA500"; // Save orange color code
        });

        boxRed.setOnClickListener(v -> {
            resetColors(); // Reset all boxes to white
            boxRed.setBackgroundColor(Color.RED); // Highlight red box
            selectedColor = "#FF0000"; // Save red color code
        });


        // Initialize UI elements
        AutoCompleteTextView spinnerReviewType = findViewById(R.id.spinnerReviewType);

        // Define the list of options for the dropdown
        String[] reviewTypes = new String[]{"Place", "Route", "Other"};

        // Create an ArrayAdapter using the reviewTypes array
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                reviewTypes
        );

        // Set the adapter to the dropdown
        spinnerReviewType.setAdapter(adapter);

        // Make sure the dropdown is visible when clicked
        spinnerReviewType.setOnClickListener(v -> spinnerReviewType.showDropDown());

        // Set up the Submit button click listener
        btnSubmitReview.setOnClickListener(v -> submitReview());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                // Update the EditText with the place’s address (or name if you prefer)
                etPlaceName.setText(place.getAddress());
                Log.i("LeaveReviewActivity", "Place Selected: " + place.getName() + " (" + place.getId() + ")");
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(this, "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                Log.e("LeaveReviewActivity", "Autocomplete error: " + status.getStatusMessage());
            }
        }
    }


    // Reset all color boxes to white
    private void resetColors() {
        boxGreen.setBackgroundColor(Color.parseColor("#A9A9A9"));
        boxOrange.setBackgroundColor(Color.parseColor("#A9A9A9"));
        boxRed.setBackgroundColor(Color.parseColor("#A9A9A9"));
    }

    // Submit the review to Firebase
    private void submitReview() {
        String placeName = etPlaceName.getText().toString().trim();
        String reviewText = etReview.getText().toString().trim();
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "anonymous";

        if (placeName.isEmpty() || reviewText.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a unique ID for the review
        String reviewId = databaseRef.push().getKey();

        if (reviewId != null) {
            Map<String, Object> reviewData = new HashMap<>();
            reviewData.put("placeName", placeName);
            reviewData.put("reviewText", reviewText);
            reviewData.put("selectedColor", selectedColor);
            reviewData.put("userId", userId);
            reviewData.put("timestamp", System.currentTimeMillis());

            // Save the review to Firebase
            databaseRef.child(reviewId).setValue(reviewData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(LeaveReviewActivity.this, "Review submitted successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(LeaveReviewActivity.this, "Failed to submit review: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Failed to generate review ID", Toast.LENGTH_SHORT).show();
        }
    }
}
