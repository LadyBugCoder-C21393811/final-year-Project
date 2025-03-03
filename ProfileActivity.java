package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    // Declare UI elements
    private TextView usernameTextView, dobTextView, placeOfOriginTextView, disabilityTypeTextView, mobilityAidTextView, otherMobilityAidTextView;
    private Button editProfileButton;

    // Firebase reference
    private DatabaseReference databaseReference;

    public FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize UI elements
        usernameTextView = findViewById(R.id.usernameTextView);
        dobTextView = findViewById(R.id.dobTextView);
        placeOfOriginTextView = findViewById(R.id.placeOfOriginTextView);
        disabilityTypeTextView = findViewById(R.id.disabilityTypeTextView);
        mobilityAidTextView = findViewById(R.id.mobilityAidTextView);
        otherMobilityAidTextView = findViewById(R.id.otherMobilityAidTextView);
        editProfileButton = findViewById(R.id.editProfileButton);

        // Set a click listener for the Edit Profile button
        editProfileButton.setOnClickListener(v -> {
            // Navigate to EditProfileActivity
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
           // Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);

        });

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Log.d("FirebaseAuth", "User UID : " + currentUser.getUid());
            Log.d("FirebaseAuth", "User Email: " + currentUser.getEmail());
        } else {
            Log.e("FirebaseAuth", "User not authenticated. Redirecting to login.");
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        }

        // Retrieve user ID from Intent
        String userId = currentUser.getUid();

        if (userId != null && !userId.isEmpty()) {
            // Fetch user data
            retrieveUserData(userId);
        } else {
            // Handle missing user ID
            Toast.makeText(this, "No user ID provided.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "User ID is null or empty");
        }
    }

    private void retrieveUserData(String userId) {
        Log.d(TAG, "Retrieving user data for userId: " + userId);

        // Query the UserProfiles node
        databaseReference.child("UserProfiles").orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Log.d(TAG, "DataSnapshot exists: " + dataSnapshot.toString());

                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                // Map the data to the User object
                                User user = userSnapshot.getValue(User.class);
                                if (user != null) {
                                    // Update UI with user data
                                    usernameTextView.setText("Username: " + user.username);
                                    dobTextView.setText("Date of Birth: " + user.dob);
                                    placeOfOriginTextView.setText("Place of Origin: " + user.placeOfOrigin);
                                    disabilityTypeTextView.setText("Disability Type: " + user.disabilityType);
                                    mobilityAidTextView.setText("Mobility Aid: " + user.mobilityAid);
                                    otherMobilityAidTextView.setText("Other Mobility Aid: " + user.otherMobilityAid);
                                    Log.d(TAG, "User data loaded successfully.");
                                    break; // Exit the loop after finding the matching user
                                } else {
                                    Log.e(TAG, "User object is null");
                                    Toast.makeText(ProfileActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Log.e(TAG, "No user found with userId: " + userId);
                            Toast.makeText(ProfileActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "Database error: ", databaseError.toException());
                        Toast.makeText(ProfileActivity.this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // User model class
    public static class User {
        public String username, dob, placeOfOrigin, disabilityType, mobilityAid, otherMobilityAid;

        // Default constructor required for Firebase
        public User() {
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
