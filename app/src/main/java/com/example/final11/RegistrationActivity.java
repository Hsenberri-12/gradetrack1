package com.example.final11;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class RegistrationActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // add to Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        etEmail = findViewById( R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();


                if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(RegistrationActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }

                else if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegistrationActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {

                    checkIfEmailExists(email, password);
                }
            }
        });
    }

    private void checkIfEmailExists(final String email, final String password) {
        mDatabase.child("users").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Toast.makeText(RegistrationActivity.this, "Email already registered. Please log in or use a different email.", Toast.LENGTH_SHORT).show();
                } else {
                    createUser(email, password);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors
                Toast.makeText(RegistrationActivity.this, "Error checking email: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createUser(String email, String password) {

        String userId = mDatabase.push().getKey();
        if (userId != null) {
            //  userType 0 for (students)
            int userType = 0;

            // Create User
            User user = new User(email, password, userType);

            // Save the user data to Firebase
            mDatabase.child("users").child(userId).setValue(user)
                    .addOnSuccessListener(aVoid -> {

                        Toast.makeText(RegistrationActivity.this, "Registration successful! You can login now", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> {

                        Toast.makeText(RegistrationActivity.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

}
