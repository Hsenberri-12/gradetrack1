package com.example.final11;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private EditText studentIdInput;
    private Button btnShowGrades;
    private Button btnLogout;
    private TextView tvGrade;
    private TableLayout tableSchedule;

    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        tvGrade = findViewById(R.id.tvGrade);
        studentIdInput = findViewById(R.id.etStudentId);
        btnShowGrades = findViewById(R.id.btnShowGrades);
        btnLogout = findViewById(R.id.btnLogout);
        tableSchedule = findViewById(R.id.tableSchedule);

        // Reference to Firebase Database "students"
        database = FirebaseDatabase.getInstance().getReference("students");

        btnShowGrades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String studentId = studentIdInput.getText().toString().trim();

                if (studentId.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter a Student ID", Toast.LENGTH_SHORT).show();
                } else {
                    fetchStudentData(studentId);
                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    private void fetchStudentData(String studentId) {

        tvGrade.setText("");
        tableSchedule.removeAllViews();

        database.orderByChild("studentId").equalTo(studentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                        String name = studentSnapshot.child("name").getValue(String.class);
                        String major = studentSnapshot.child("major").getValue(String.class);
                        String course = studentSnapshot.child("course").getValue(String.class);
                        String grades = studentSnapshot.child("grade").getValue(String.class);

                        tvGrade.setText("Grades: " + grades);

                        TableRow row = new TableRow(MainActivity.this);
                        TextView idText = new TextView(MainActivity.this);
                        idText.setText(studentId);
                        idText.setPadding(8, 8, 8, 8);

                        TextView nameText = new TextView(MainActivity.this);
                        nameText.setText(name);
                        nameText.setPadding(8, 8, 8, 8);

                        TextView majorText = new TextView(MainActivity.this);
                        majorText.setText(major);
                        majorText.setPadding(8, 8, 8, 8);

                        TextView courseText = new TextView(MainActivity.this);
                        courseText.setText(course);
                        courseText.setPadding(8, 8, 8, 8);

                        TextView gradeText = new TextView(MainActivity.this);
                        gradeText.setText(grades);
                        gradeText.setPadding(8, 8, 8, 8);

                        row.addView(idText);
                        row.addView(nameText);
                        row.addView(majorText);
                        row.addView(courseText);
                        row.addView(gradeText);

                        tableSchedule.addView(row);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Student not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error retrieving data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logoutUser() {
        // Perform logout logic
        Toast.makeText(MainActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
