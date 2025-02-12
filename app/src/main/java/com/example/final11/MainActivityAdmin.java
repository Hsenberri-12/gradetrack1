package com.example.final11;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivityAdmin extends AppCompatActivity {

    private Button btnAdd, btnAddAdmin,btnaddstudents, btnLogout, btnEdit, btnDelete;
    private DatabaseReference databaseRef;
    private TableLayout tableScheduleAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        databaseRef = FirebaseDatabase.getInstance().getReference("students");
        tableScheduleAdmin = findViewById(R.id.tableScheduleAdmin);

        // UI elements
        btnAddAdmin = findViewById(R.id.btnAddAdmin);
        btnaddstudents = findViewById(R.id.btnaddstudents);
        btnAdd = findViewById(R.id.btnAdd);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnLogout = findViewById(R.id.btnLogout);

        fetchStudentsData();


        btnAddAdmin.setOnClickListener(v -> {
            // to move to AddAdminActivity
            Intent intent = new Intent(MainActivityAdmin.this, AddAdminActivity.class);
            startActivity(intent);
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAddStudentDialog();
            }
        });

        // Logout Button
        btnLogout.setOnClickListener(v -> {
            Toast.makeText(MainActivityAdmin.this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivityAdmin.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        btnaddstudents.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivityAdmin.this, RegistrationActivity.class);  // Update this with your RegistrationActivity
            startActivity(intent);
        });
    }

    // Fetch students data from Firebase
    private void fetchStudentsData() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tableScheduleAdmin.removeAllViews(); // Clear the existing rows

                // Table Header
                TableRow headerRow = new TableRow(MainActivityAdmin.this);
                String[] headerColumns = {"ID", "Name", "Major", "Course", "Grade", "Actions"};
                for (String column : headerColumns) {
                    TextView textView = new TextView(MainActivityAdmin.this);
                    textView.setText(column);
                    textView.setPadding(8, 8, 8, 8);
                    headerRow.addView(textView);
                }
                tableScheduleAdmin.addView(headerRow);

                // Loop through each student data in Firebase and create a new row for each
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Student student = snapshot.getValue(Student.class);
                    String uniqueId = snapshot.getKey(); // This is the Firebase unique ID (OIM7BeC2M1JRwuiP6pB)

                    if (student != null) {
                        TableRow tableRow = new TableRow(MainActivityAdmin.this);

                        // Add student data to the row
                        tableRow.addView(createTextView(student.getStudentId()));  // Toast the unique ID here
                        tableRow.addView(createTextView(student.getName()));
                        tableRow.addView(createTextView(student.getMajor()));
                        tableRow.addView(createTextView(student.getCourse()));
                        tableRow.addView(createTextView(student.getGrade()));

                        // Add action buttons (Edit and Delete)
                        LinearLayout actionsLayout = new LinearLayout(MainActivityAdmin.this);
                        actionsLayout.setOrientation(LinearLayout.HORIZONTAL);

                        Button btnEdit = new Button(MainActivityAdmin.this);
                        btnEdit.setText("Edit");
                        btnEdit.setOnClickListener(v -> editStudent(student, uniqueId));

                        Button btnDelete = new Button(MainActivityAdmin.this);
                        btnDelete.setText("Delete");
                        btnDelete.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                        btnDelete.setOnClickListener(v -> deleteStudent(uniqueId));

                        actionsLayout.addView(btnEdit);
                        actionsLayout.addView(btnDelete);

                        tableRow.addView(actionsLayout);


                        tableScheduleAdmin.addView(tableRow);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivityAdmin.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(MainActivityAdmin.this);
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        return textView;
    }

    private void editStudent(Student student, String id) {
        // Create a dialog edit student
        Dialog dialog = new Dialog(MainActivityAdmin.this);
        dialog.setContentView(R.layout.dialog_edit_student);


        EditText etStudentId = dialog.findViewById(R.id.etStudentId);
        EditText etName = dialog.findViewById(R.id.etName);
        EditText etMajor = dialog.findViewById(R.id.etMajor);
        EditText etCourse = dialog.findViewById(R.id.etCourse);
        EditText etGrade = dialog.findViewById(R.id.etGrade);
        Button btnSubmit = dialog.findViewById(R.id.btnSubmit);

        // give data to the dilog
        etStudentId.setText(student.getStudentId());
        etName.setText(student.getName());
        etMajor.setText(student.getMajor());
        etCourse.setText(student.getCourse());
        etGrade.setText(student.getGrade());

        // Disable the ID and Name fields for editing
        etStudentId.setEnabled(false);
        etName.setEnabled(false);

        // Submit button
        btnSubmit.setOnClickListener(v -> {
            String major = etMajor.getText().toString().trim();
            String course = etCourse.getText().toString().trim();
            String grade = etGrade.getText().toString().trim();

            if (major.isEmpty() || course.isEmpty() || grade.isEmpty()) {
                Toast.makeText(MainActivityAdmin.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                String studentId = student.getStudentId();
                updateStudentInFirebase(id, studentId, major, course, grade);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void updateStudentInFirebase(String id, String studentId, String major, String course, String grade) {
        if (id == null || id.isEmpty()) {
            Toast.makeText(MainActivityAdmin.this, "Error: Invalid student ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // First, check if the course already exists for the same studentId but in a different record
        databaseRef.orderByChild("studentId").equalTo(studentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean courseExists = false;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String existingId = snapshot.getKey(); // Firebase unique ID
                    Student existingStudent = snapshot.getValue(Student.class);

                    if (existingStudent != null && !existingId.equals(id)) {
                        if (existingStudent.getCourse().equals(course)) {
                            courseExists = true;
                            break;
                        }
                    }
                }

                if (courseExists) {
                    Toast.makeText(MainActivityAdmin.this, "This student is already enrolled in this course", Toast.LENGTH_SHORT).show();
                } else {
                    // for update data
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("major", major);
                    updates.put("course", course);
                    updates.put("grade", grade);

                    databaseRef.child(id).updateChildren(updates)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(MainActivityAdmin.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                                fetchStudentsData();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(MainActivityAdmin.this, "Error updating student", Toast.LENGTH_SHORT).show();
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivityAdmin.this, "Error checking course", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void deleteStudent(String id) {
        new android.app.AlertDialog.Builder(MainActivityAdmin.this)
                .setTitle("Delete Student")
                .setMessage("Are you sure you want to delete this student?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    databaseRef.child(id).removeValue()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(MainActivityAdmin.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                fetchStudentsData();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(MainActivityAdmin.this, "Error deleting student", Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("No", null)
                .show();
    }



    private void showAddStudentDialog() {
        // Create a new dialog and set the layout
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_student);

        // Define UI elements
        EditText etStudentId = dialog.findViewById(R.id.etStudentId);
        EditText etName = dialog.findViewById(R.id.etName);
        EditText etMajor = dialog.findViewById(R.id.etMajor);
        EditText etCourse = dialog.findViewById(R.id.etCourse);
        EditText etGrade = dialog.findViewById(R.id.etGrade);
        Button btnSubmit = dialog.findViewById(R.id.btnSubmit);


        etStudentId.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String studentId = etStudentId.getText().toString().trim();
                if (!studentId.isEmpty()) {
                    checkIfStudentIdExists(studentId, etName);
                }
            }
        });

        btnSubmit.setOnClickListener(v -> {
            String studentId = etStudentId.getText().toString().trim();
            String name = etName.getText().toString().trim();
            String major = etMajor.getText().toString().trim();
            String course = etCourse.getText().toString().trim();
            String grade = etGrade.getText().toString().trim();

            if (studentId.isEmpty() || name.isEmpty() || major.isEmpty() || course.isEmpty() || grade.isEmpty()) {
                Toast.makeText(MainActivityAdmin.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                addStudentToFirebase(studentId, name, major, course, grade);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void checkIfStudentIdExists(final String studentId, final EditText etName) {

        databaseRef.orderByChild("studentId").equalTo(studentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // If the ID exists get the name
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Student existingStudent = snapshot.getValue(Student.class);
                        if (existingStudent != null) {
                            etName.setText(existingStudent.getName());
                            etName.setEnabled(false);
                            return;
                        }
                    }
                } else {
                    // If ID is not found, keep name field editable
                    etName.setText("");
                    etName.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivityAdmin.this, "Error checking student ID: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addStudentToFirebase(String studentId, String name, String major, String course, String grade) {
        databaseRef.orderByChild("studentId").equalTo(studentId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot result = task.getResult();
                if (result.exists()) {
                    boolean courseExists = false;

                    for (DataSnapshot snapshot : result.getChildren()) {
                        Student existingStudent = snapshot.getValue(Student.class);
                        if (existingStudent != null && existingStudent.getCourse().equals(course)) {
                            courseExists = true;
                            break;
                        }
                    }

                    if (courseExists) {
                        Toast.makeText(MainActivityAdmin.this, "This student is already enrolled in this course", Toast.LENGTH_SHORT).show();
                    } else {
                        createNewStudent(studentId, name, major, course, grade);
                    }
                } else {
                    createNewStudent(studentId, name, major, course, grade);
                }
            } else {
                Toast.makeText(MainActivityAdmin.this, "Error checking student ID: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createNewStudent(String studentId, String name, String major, String course, String grade) {
        String uniqueId = databaseRef.push().getKey();

        if (uniqueId != null) {
            Student student = new Student(studentId, name, major, course, grade);
            databaseRef.child(uniqueId).setValue(student)
                    .addOnSuccessListener(aVoid -> Toast.makeText(MainActivityAdmin.this, "Student added successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(MainActivityAdmin.this, "Failed to add student", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(MainActivityAdmin.this, "Error generating unique ID", Toast.LENGTH_SHORT).show();
        }
    }


}
