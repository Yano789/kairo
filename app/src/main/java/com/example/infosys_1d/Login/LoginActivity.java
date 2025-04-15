package com.example.infosys_1d.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.infosys_1d.MainActivity;
import com.example.infosys_1d.R;
import java.math.BigInteger;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText usernameEditText, passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        // Initialize views
        usernameEditText = findViewById(R.id.username_input);
        passwordEditText = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_btn);

        loginButton.setOnClickListener(v -> {
            String usernameInput = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (usernameInput.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            Object authenticatedUser = getAuthenticatedUser(usernameInput, password);
            if (authenticatedUser != null) {
                String userEmail = authenticatedUser instanceof Student
                        ? ((Student) authenticatedUser).getEmail()
                        : ((Admin) authenticatedUser).getEmail();

                // Save email to SharedPreferences
                SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("user_email", userEmail);
                editor.apply();
                Log.d(TAG, "Login successful, saved email: " + userEmail);

                // Start MainActivity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("user_email", userEmail);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Object getAuthenticatedUser(String usernameInput, String password) {
        // Check admins by ID or email
        Admin admin = getAuthenticatedAdmin(usernameInput, password);
        if (admin != null) {
            return admin;
        }
        // Check students by ID or email
        Student student = getAuthenticatedStudent(usernameInput, password);
        return student;
    }

    private Student getAuthenticatedStudent(String usernameInput, String password) {
        // Try as numeric ID first
        try {
            BigInteger inputId = new BigInteger(usernameInput);
            return checkStudentById(inputId, password);
        } catch (NumberFormatException e) {
            // Then try as email/name
            return checkStudentByEmail(usernameInput, password);
        }
    }

    private Student checkStudentByEmail(String email, String password) {
        ArrayList<Student> students = UserRepository.getSampleStudents();
        for (Student student : students) {
            if ((student.getEmail().equalsIgnoreCase(email) || student.getName().equalsIgnoreCase(email))
                    && student.getPassword().equals(password)) {
                Log.d(TAG, "Authenticated student by email/name: " + email);
                return student;
            }
        }
        Log.w(TAG, "No student found for email/name: " + email);
        return null;
    }

    private Student checkStudentById(BigInteger id, String password) {
        ArrayList<Student> students = UserRepository.getSampleStudents();
        for (Student student : students) {
            if (student.getId().equals(id) && student.getPassword().equals(password)) {
                Log.d(TAG, "Authenticated student by ID: " + id);
                return student;
            }
        }
        Log.w(TAG, "No student found for ID: " + id);
        return null;
    }

    private Admin getAuthenticatedAdmin(String input, String password) {
        ArrayList<Admin> admins = UserRepository.getSampleAdmins();
        for (Admin admin : admins) {
            if ((admin.getAdminId().equalsIgnoreCase(input) || admin.getEmail().equalsIgnoreCase(input))
                    && admin.getPassword().equals(password)) {
                Log.d(TAG, "Authenticated admin by " + (admin.getAdminId().equalsIgnoreCase(input) ? "ID: " + input : "email: " + input));
                return admin;
            }
        }
        Log.w(TAG, "No admin found for input: " + input);
        return null;
    }
}