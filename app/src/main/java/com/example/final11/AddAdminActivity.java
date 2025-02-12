package com.example.final11;

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

public class AddAdminActivity extends AppCompatActivity {


    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnAddAdmin;

    // Firebase Database reference
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_admin);

        // add to Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();


        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnAddAdmin = findViewById(R.id.btnAddAdmin);


        btnAddAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(AddAdminActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
                else if (!password.equals(confirmPassword)) {
                    Toast.makeText(AddAdminActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {
                    checkIfEmailExists(email, password);
                }
            }
        });
    }

    // to check if the email already exists
    private void checkIfEmailExists(final String email, final String password) {
        mDatabase.child("users").orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            Toast.makeText(AddAdminActivity.this, "Email already exists. Please choose a different email.", Toast.LENGTH_SHORT).show();
                        } else {
                            addAdminToFirebase(email, password);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        Toast.makeText(AddAdminActivity.this, "Error checking email: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // to add an admin user to Firebase
    private void addAdminToFirebase(String email, String password) {
        String userId = mDatabase.push().getKey();

        if (userId != null) {
            User user = new User(email, password, 1);

            mDatabase.child("users").child(userId).setValue(user)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AddAdminActivity.this, "Admin added successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddAdminActivity.this, "Failed to add admin: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(AddAdminActivity.this, "Failed to generate user ID", Toast.LENGTH_SHORT).show();
        }
    }
}
