package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SetupProfileActivity extends AppCompatActivity {

    private static final String TAG = "SetupProfileActivity";

    private EditText usernameEditText, dobText, placeOfOriginText, disabilityTypeText, otherMobilityAidText;
    private Spinner mobilityAidsSpinner;
    private Button continueButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called - SetupProfileActivity");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setup_profile);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Initialize views by finding them by ID
        usernameEditText = findViewById(R.id.usernameEditText);
        dobText = findViewById(R.id.dobText);
        placeOfOriginText = findViewById(R.id.placeOfOriginText);
        disabilityTypeText = findViewById(R.id.disabilityTypeText);
        otherMobilityAidText = findViewById(R.id.otherMobilityiop);
        mobilityAidsSpinner = findViewById(R.id.mobilityAidsSpinner);
        continueButton = findViewById(R.id.buttonncontinue);

        // Set up the mobility aids array
        String[] mobilityAidsArray = {"Manual Wheelchair", "Electric Wheelchair", "Mobility Scooter", "Walking Frame", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mobilityAidsArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mobilityAidsSpinner.setAdapter(adapter);

        mobilityAidsSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item
                String selectedItem = (String) parent.getItemAtPosition(position);

                // Show the "Other" EditText if the selected item is "Other"
                if (selectedItem.equals("Other")) {
                    otherMobilityAidText.setVisibility(View.VISIBLE);
                    Log.d(TAG, "\"Other\" selected - showing additional input field.");
                } else {
                    otherMobilityAidText.setVisibility(View.GONE);
                    Log.d(TAG, "\"Other\" not selected - hiding additional input field.");
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // If nothing is selected, hide the "Other" EditText
                otherMobilityAidText.setVisibility(View.GONE);
                Log.d(TAG, "No item selected - hiding additional input field.");
            }
        });

        // Set up the continue button click listener
        continueButton.setOnClickListener(v -> {
            // Collect the data from the form
            String username = usernameEditText.getText().toString().trim();
            String dob = dobText.getText().toString().trim();
            String placeOfOrigin = placeOfOriginText.getText().toString().trim();
            String disabilityType = disabilityTypeText.getText().toString().trim();
            String mobilityAid = mobilityAidsSpinner.getSelectedItem().toString();
            String otherMobilityAid = otherMobilityAidText.getText().toString().trim();

            if (validateForm(username, dob, placeOfOrigin, disabilityType)) {
                saveUserData(username, dob, placeOfOrigin, disabilityType, mobilityAid, otherMobilityAid);
            }
        });
    }

    private boolean validateForm(String username, String dob, String placeOfOrigin, String disabilityType) {
        if (username.isEmpty() || dob.isEmpty() || placeOfOrigin.isEmpty() || disabilityType.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveUserData(String username, String dob, String placeOfOrigin, String disabilityType, String mobilityAid, String otherMobilityAid) {
        // Create a unique key for each user
        String userId = databaseReference.push().getKey();

        // Create a User object
        User user = new User(username, dob, placeOfOrigin, disabilityType, mobilityAid, otherMobilityAid);

        if (userId != null) {
            // Save the user to Firebase
            databaseReference.child(userId).setValue(user)
                    .addOnSuccessListener(aVoid -> {
                        // Data saved successfully
                        Toast.makeText(SetupProfileActivity.this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();

                        // Navigate to activity_home_page
                        Intent intent = new Intent(SetupProfileActivity.this, HomePageActivity.class);
                        startActivity(intent);
                        finish(); // To prevent going back to this activity
                    })
                    .addOnFailureListener(e -> {
                        // Failed to save data
                        Toast.makeText(SetupProfileActivity.this, "Failed to save profile. Try again.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error saving data: ", e);
                    });
        } else {
            Toast.makeText(this, "Unable to generate User ID. Try again.", Toast.LENGTH_SHORT).show();
        }
    }

    // User data class to store the information
    public static class User {
        public String username, dob, placeOfOrigin, disabilityType, mobilityAid, otherMobilityAid;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String username, String dob, String placeOfOrigin, String disabilityType, String mobilityAid, String otherMobilityAid) {
            this.username = username;
            this.dob = dob;
            this.placeOfOrigin = placeOfOrigin;
            this.disabilityType = disabilityType;
            this.mobilityAid = mobilityAid;
            this.otherMobilityAid = otherMobilityAid;
        }
    }
}
