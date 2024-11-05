package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class Longin extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button loginButton, signUpButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_longin);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Find UI elements based on their IDs in the layout
        inputEmail = findViewById(R.id.editTextText4);  // Email input
        inputPassword = findViewById(R.id.editTextText6);  // Password input
        loginButton = findViewById(R.id.button3);  // Login button
        signUpButton = findViewById(R.id.button5);  // Sign-up button

        // Handle system window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.HomePage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Handle login button click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Longin.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if email exists and password is correct in Firebase
                loginUser(email, password);
            }
        });

        // Handle sign-up button click
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to MainActivity (sign-up screen)
                Intent intent = new Intent(Longin.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    // Method to log in the user
    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Email exists and user logged in successfully, navigate to ActivityHomeScreen
                        Intent intent = new Intent(Longin.this, HomePageActivity.class);  // Navigating to ActivityHomeScreen
                        startActivity(intent);
                        finish();  // Close the login activity
                    } else {
                        // Login failed, handle different error cases
                        if (task.getException() != null) {
                            String errorMessage = task.getException().getMessage();

                            if (errorMessage != null) {
                                if (errorMessage.contains("There is no user record")) {
                                    // No user with this email
                                    Toast.makeText(Longin.this, "Email doesn't exist, try signing up.", Toast.LENGTH_SHORT).show();
                                } else if (errorMessage.contains("The password is invalid")) {
                                    // Incorrect password
                                    Toast.makeText(Longin.this, "Incorrect password, please try again.", Toast.LENGTH_SHORT).show();
                                } else {
                                    // General error
                                    Toast.makeText(Longin.this, "Login failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });
    }
}
