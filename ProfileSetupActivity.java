package com.example.fyp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileSetupActivity extends AppCompatActivity {
    private EditText usernameEditText, dobText, placeOfOriginText, disabilityTypeText, mobilityAidsText;
    private RadioGroup genderRadioGroup;
    private Button buttonncontinue;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("UserProfiles");

        // Find UI elements based on their IDs in the layout
        usernameEditText = findViewById(R.id.usernameEditText);  // input user name
        dobText = findViewById(R.id.dobText);  // input dob
        placeOfOriginText = findViewById(R.id.placeOfOriginText);  // input place of origin
        disabilityTypeText = findViewById(R.id.disabilityTypeText);  // input disability type
        mobilityAidsText = findViewById(R.id.mobilityAidsText);  // input mobility aids
        genderRadioGroup = findViewById(R.id.genderRadioGroup);  // radioGroup for gender options
        buttonncontinue = findViewById(R.id.buttonncontinue);

        buttonncontinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String dob = dobText.getText().toString().trim();
                String placeOfOrigin = placeOfOriginText.getText().toString().trim();
                String disabilityType = disabilityTypeText.getText().toString().trim();
                String mobilityAids = mobilityAidsText.getText().toString().trim();
                int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();

                String gender = "";
                if (selectedGenderId != -1) {
                    RadioButton selectedGenderButton = findViewById(selectedGenderId);
                    gender = selectedGenderButton.getText().toString();
                }

                // Validate inputs before sending to Firebase
                if (username.isEmpty() || dob.isEmpty() || placeOfOrigin.isEmpty()) {
                    Toast.makeText(ProfileSetupActivity.this, "Please fill out all required fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                UserProfile userProfile = new UserProfile(username, dob, placeOfOrigin, disabilityType, mobilityAids, gender);
                databaseReference.push().setValue(userProfile).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileSetupActivity.this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileSetupActivity.this, "Failed to save profile. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
