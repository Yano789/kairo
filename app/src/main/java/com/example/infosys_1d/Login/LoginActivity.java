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

            Student authenticatedStudent = getAuthenticatedStudent(usernameInput, password);
            if (authenticatedStudent != null) {
                // Save email to SharedPreferences
                SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("user_email", authenticatedStudent.getEmail());
                editor.apply();
                Log.d(TAG, "Login successful, saved email: " + authenticatedStudent.getEmail());

                // Pass email to MainActivity (optional, kept for compatibility)
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("user_email", authenticatedStudent.getEmail());
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Student getAuthenticatedStudent(String usernameInput, String password) {
        // Check if input is an ID (numeric)
        try {
            BigInteger inputId = new BigInteger(usernameInput);
            return checkById(inputId, password);
        } catch (NumberFormatException e) {
            // If not a number, treat as email/username
            return checkByEmail(usernameInput, password);
        }
    }

    private Student checkByEmail(String email, String password) {
        ArrayList<Student> students = UserRepository.getSampleStudents();
        for (Student student : students) {
            if ((student.getEmail().equalsIgnoreCase(email) || student.getName().equalsIgnoreCase(email))
                    && student.getPassword().equals(password)) {
                Log.d(TAG, "Authenticated by email/name: " + email);
                return student;
            }
        }
        Log.w(TAG, "No student found for email/name: " + email);
        return null;
    }

    private Student checkById(BigInteger id, String password) {
        ArrayList<Student> students = UserRepository.getSampleStudents();
        for (Student student : students) {
            if (student.getId().equals(id) && student.getPassword().equals(password)) {
                Log.d(TAG, "Authenticated by ID: " + id);
                return student;
            }
        }
        Log.w(TAG, "No student found for ID: " + id);
        return null;
    }
}