package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    // Firebase reference
    private DatabaseReference databaseReference;
    private TextView usernameEditText ,placeOfOriginEditText, inputEmail,dobTextView;
    private Button saveProfileButton;

    public FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        // Initialize UI elements
        usernameEditText = findViewById(R.id.usernameEditText);
        placeOfOriginEditText= findViewById(R.id.placeOfOriginEditText);

        dobTextView = findViewById(R.id.dobTextView);
        saveProfileButton= findViewById(R.id.saveProfileButton);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

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
            startActivity(new Intent(EditProfileActivity.this, LoginActivity.class));
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

        saveProfileButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String dob = dobTextView.getText().toString().trim();
            String placeOfOrigin = placeOfOriginEditText.getText().toString().trim();

            if (username.isEmpty() || dob.isEmpty() || placeOfOrigin.isEmpty()) {
                Toast.makeText(EditProfileActivity.this, "Please fill all the fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            saveOrUpdateUserData(currentUser.getUid(), username, dob, placeOfOrigin);
        });

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
                                ProfileActivity.User user = userSnapshot.getValue(ProfileActivity.User.class);
                                if (user != null) {
                                    // Update UI with user data
                                    usernameEditText.setText(   user.username);
                                    dobTextView.setText(  user.dob);
                                    placeOfOriginEditText.setText( user.placeOfOrigin);

                                    Log.d(TAG, "User data loaded successfully.");
                                    break; // Exit the loop after finding the matching user
                                } else {
                                    Log.e(TAG, "User object is null");
                                    Toast.makeText(EditProfileActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Log.e(TAG, "No user found with userId: " + userId);
                            Toast.makeText(EditProfileActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "Database error: ", databaseError.toException());
                        Toast.makeText(EditProfileActivity.this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void saveOrUpdateUserData(String userId, String username, String dob, String placeOfOrigin) {
        // Check if userId is provided
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Invalid User ID. Unable to update profile.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a User object
        UserProfile user = new UserProfile(username, dob, placeOfOrigin,currentUser.getUid());

        // Update the user details in Firebase
        databaseReference.child("UserProfiles").child(userId).setValue(user)
                .addOnSuccessListener(aVoid -> {
                    // Data updated successfully
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();

                    // Navigate to activity_home_page
                    Intent intent = new Intent(EditProfileActivity.this, HomePageActivity.class);
                    startActivity(intent);
                    finish(); // To prevent going back to this activity
                })
                .addOnFailureListener(e -> {
                    // Failed to update data
                    Toast.makeText(EditProfileActivity.this, "Failed to update profile. Try again.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error updating data: ", e);
});
}
}