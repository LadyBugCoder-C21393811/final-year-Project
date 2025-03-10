//zzpackage com.example.fyp;

//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.Toast;

//mport androidx.appcompat.app.AppCompatActivity;

//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;

//public class ProfileSetupActivity extends AppCompatActivity {
  //  private EditText usernameEditText, dobText, placeOfOriginText, disabilityTypeText, mobilityAidsText;
    //private RadioGroup genderRadioGroup;
    //private Button buttonncontinue;
    //private DatabaseReference databaseReference;

    //@Override
    //protected void onCreate(Bundle savedInstanceState) {
      //  super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_setup_profile);

        // Initialize Firebase Database reference
        //databaseReference = FirebaseDatabase.getInstance().getReference("UserProfiles");

        // Find UI elements based on their IDs in the layout
        //usernameEditText = findViewById(R.id.usernameEditText);  // input user name
        //dobText = findViewById(R.id.dobText);  // input dob
        //placeOfOriginText = findViewById(R.id.placeOfOriginText);  // input place of origin
        //disabilityTypeText = findViewById(R.id.disabilityTypeText);  // input disability type
        //genderRadioGroup = findViewById(R.id.genderRadioGroup);  // radioGroup for gender options
        //buttonncontinue = findViewById(R.id.buttonncontinue);

        //buttonncontinue.setOnClickListener(new View.OnClickListener() {
          //  @Override
            //public void onClick(View v) {
              //  String username = usernameEditText.getText().toString().trim();
                //String dob = dobText.getText().toString().trim();
                //String placeOfOrigin = placeOfOriginText.getText().toString().trim();
                //String disabilityType = disabilityTypeText.getText().toString().trim();
                //String mobilityAids = mobilityAidsText.getText().toString().trim();
                //int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();

                //String gender = "";
                //if (selectedGenderId != -1) {
                  //  RadioButton selectedGenderButton = findViewById(selectedGenderId);
                    //gender = selectedGenderButton.getText().toString();
                //}

                // Validate inputs before sending to Firebase
                //if (username.isEmpty() || dob.isEmpty() || placeOfOrigin.isEmpty()) {
                  //  Toast.makeText(ProfileSetupActivity.this, "Please fill out all required fields.", Toast.LENGTH_SHORT).show();
                    //return;
                //}

                //UserProfile userProfile = new UserProfile(username, dob, placeOfOrigin, disabilityType, mobilityAids, gender);
                //databaseReference.push().setValue(userProfile).addOnCompleteListener(task -> {
                 //   if (task.isSuccessful()) {
                  //      Toast.makeText(ProfileSetupActivity.this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
                    //} else {
                      //  Toast.makeText(ProfileSetupActivity.this, "Failed to save profile. Please try again.", Toast.LENGTH_SHORT).show();
                    //}
                //});
            //}
        //});
    //}
//}

// test code
/*package com.example.fyp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileSetupActivity extends AppCompatActivity {
    private EditText usernameEditText, dobText, placeOfOriginText, disabilityTypeText;
    private RadioGroup genderRadioGroup;
    private Spinner mobilityAidsSpinner;
    private Button buttonContinue;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("UserProfiles");

        // Find UI elements based on their IDs in the layout
        usernameEditText = findViewById(R.id.usernameEditText); // input username
        dobText = findViewById(R.id.dobText); // input dob
        placeOfOriginText = findViewById(R.id.placeOfOriginText); // input place of origin
        disabilityTypeText = findViewById(R.id.disabilityTypeText); // input disability type
        genderRadioGroup = findViewById(R.id.genderRadioGroup); // radioGroup for gender options
        mobilityAidsSpinner = findViewById(R.id.mobilityAidsSpinner); // Spinner for mobility aids
        buttonContinue = findViewById(R.id.buttonncontinue);

        // Set up the Spinner with the array data
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.mobility_aids_array, // Reference to your string-array in strings.xml
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mobilityAidsSpinner.setAdapter(adapter);

        // Set OnClickListener for the button
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String dob = dobText.getText().toString().trim();
                String placeOfOrigin = placeOfOriginText.getText().toString().trim();
                String disabilityType = disabilityTypeText.getText().toString().trim();
                String mobilityAids = mobilityAidsSpinner.getSelectedItem().toString(); // Get selected Spinner value
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

                // Create a UserProfile object
                UserProfile userProfile = new UserProfile(username, dob, placeOfOrigin, disabilityType, mobilityAids, gender);

                // Push data to Firebase
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
}*/
package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileSetupActivity extends AppCompatActivity {
    private EditText usernameEditText, dobText, placeOfOriginText, disabilityTypeText, otherMobilityEditText;
    private RadioGroup genderRadioGroup;
    private Spinner mobilityAidsSpinner;
    private Button buttonContinue;
    private DatabaseReference databaseReference;

    public FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("UserProfiles");

        // Find UI elements based on their IDs in the layout
        usernameEditText = findViewById(R.id.usernameEditText); // Input username
        dobText = findViewById(R.id.dobText); // Input DOB
        placeOfOriginText = findViewById(R.id.placeOfOriginText); // Input place of origin
        disabilityTypeText = findViewById(R.id.disabilityTypeText); // Input disability type
        genderRadioGroup = findViewById(R.id.genderRadioGroup); // Gender options
        mobilityAidsSpinner = findViewById(R.id.mobilityAidsSpinner); // Spinner for mobility aids
        otherMobilityEditText = findViewById(R.id.otherMobilityiop); // EditText for "Other" mobility aid
        buttonContinue = findViewById(R.id.buttonncontinue); // Continue button


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Log.d("FirebaseAuth", "User UID 219: " + currentUser.getUid());
            Log.d("FirebaseAuth", "User Email 220: " + currentUser.getEmail());
        } else {
            Log.e("FirebaseAuth", "222 User not authenticated. Redirecting to login.");
            startActivity(new Intent(ProfileSetupActivity.this, LoginActivity.class));
            finish();
        }

        userId =  currentUser.getUid();

        // Set up the Spinner with the array data
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.mobility_aids_array, // Reference to your string-array in strings.xml
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mobilityAidsSpinner.setAdapter(adapter);

        // Add listener for spinner selection
        mobilityAidsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.equals("Other")) {
                    // Show the EditText for "Other" mobility aid
                    otherMobilityEditText.setVisibility(View.VISIBLE);
                } else {
                    // Hide the EditText when "Other" is not selected
                    otherMobilityEditText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });

        // Set OnClickListener for the button
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String dob = dobText.getText().toString().trim();
                String placeOfOrigin = placeOfOriginText.getText().toString().trim();
                String disabilityType = disabilityTypeText.getText().toString().trim();
                String mobilityAids = mobilityAidsSpinner.getSelectedItem().toString();
                String otherMobilityAid = "";

                // Check if the "Other" EditText is visible and get its value
                if (otherMobilityEditText.getVisibility() == View.VISIBLE) {
                    otherMobilityAid = otherMobilityEditText.getText().toString().trim();
                }

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

                userId =  currentUser.getUid();

                // Create a UserProfile object
                UserProfile userProfile = new UserProfile(
                        username,
                        gender,
                        dob,
                        placeOfOrigin,
                        disabilityType,
                        mobilityAids,
                        otherMobilityAid,
                        userId
                );

                Log.d("ProfileSetupActivity", "User ID: " + userId);

                // Push data to Firebase
                databaseReference.child(userId).setValue(userProfile).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileSetupActivity.this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProfileSetupActivity.this, HomePageActivity.class);  // Navigate to HomePageActivity
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e("ProfileSetupActivity", "Error saving profile", task.getException());
                        Toast.makeText(ProfileSetupActivity.this, "Failed to save profile. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
