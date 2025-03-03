package com.example.fyp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.util.Log;
import android.view.MenuItem;
import androidx.cardview.widget.CardView;

public class HomePageActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private TextView dashboardTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Log.d("FirebaseAuth", "User UID: " + currentUser.getUid());
            Log.d("FirebaseAuth", "User Email: " + currentUser.getEmail());
        } else {
            Log.e("FirebaseAuth", "User not authenticated. Redirecting to login.");
            startActivity(new Intent(HomePageActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize TextView
        dashboardTitle = findViewById(R.id.welcomeText);
        dashboardTitle.setText("Welcome to Dashboard");

        // Initialize Bottom Navigation (Removed missing items)
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.startJourney:
                        checkLocationPermission();
                        return true;

                    case R.id.profile:
                        startActivity(new Intent(HomePageActivity.this, ProfileActivity.class));
                        return true;

                    case R.id.logout:  // Logout handling
                        mAuth.signOut();
                        Intent logoutIntent = new Intent(HomePageActivity.this, LoginActivity.class);
                        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(logoutIntent);
                        finish();
                        Toast.makeText(HomePageActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                        return true;

                    default:
                        return false;
                }
            }
        });

        // Handle Card Clicks (Replaces removed navigation items)
        CardView leaveReviewCard = findViewById(R.id.leaveReviewCard);
        CardView seeReviewsCard = findViewById(R.id.seeReviewsCard);
        CardView suggestionsCard = findViewById(R.id.suggestionsCard);

        leaveReviewCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent leaveReviewIntent = new Intent(HomePageActivity.this, LeaveReviewActivity.class);
                startActivity(leaveReviewIntent);
            }
        });

        seeReviewsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent seeReviewsIntent = new Intent(HomePageActivity.this, SeeReviewsActivity.class);
                startActivity(seeReviewsIntent);
            }
        });

        suggestionsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent suggestionsIntent = new Intent(HomePageActivity.this, SuggestionLocationsActivity.class);
                startActivity(suggestionsIntent);
            }
        });
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startJourney();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startJourney();
            } else {
                Toast.makeText(this, "Location permission is required to start your journey", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startJourney() {
        Intent startJourneyIntent = new Intent(HomePageActivity.this, SearchLocationActivity.class);
        startActivity(startJourneyIntent);
    }
}
