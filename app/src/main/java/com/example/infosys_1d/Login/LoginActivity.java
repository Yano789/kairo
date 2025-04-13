package com.example.infosys_1d.Login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.infosys_1d.MainActivity;
import com.example.infosys_1d.R;

import java.math.BigInteger;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        // Initialize views
        usernameEditText = findViewById(R.id.username_input);
        passwordEditText = findViewById(R.id.password_input);
        Button loginBtn = findViewById(R.id.login_btn);

        loginBtn.setOnClickListener(v -> {
            String usernameInput = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (usernameInput.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (validateCredentials(usernameInput, password)) {
                // Successful login
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Prevent going back to login
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateCredentials(String usernameInput, String password) {
        // Check if input is an ID (numeric)
        try {
            BigInteger inputId = new BigInteger(usernameInput);
            return checkById(inputId, password);
        } catch (NumberFormatException e) {
            // If not a number, treat as email/username
            return checkByEmail(usernameInput, password);
        }
    }

    private boolean checkByEmail(String email, String password) {
        ArrayList<Student> students = UserRepository.getSampleStudents();
        for (Student student : students) {
            if ((student.getEmail().equalsIgnoreCase(email) || student.getName().equalsIgnoreCase(email))
                    && student.getPassword().equals(password)) {
                return true;
            }
        }

        ArrayList<Admin> admins = UserRepository.getSampleAdmins();
        for (Admin admin : admins) {
            if ((admin.getEmail().equalsIgnoreCase(email) || admin.getName().equalsIgnoreCase(email))
                    && admin.getPassword().equals(password)) {
                return true;
            }
        }

        return false;
    }

    private boolean checkById(BigInteger id, String password) {
        ArrayList<Student> students = UserRepository.getSampleStudents();
        for (Student student : students) {
            if (student.getId().equals(id) && student.getPassword().equals(password)) {
                return true;
            }
        }

        ArrayList<Admin> admins = UserRepository.getSampleAdmins();
        for (Admin admin : admins) {
            if (admin.getId().equals(id) && admin.getPassword().equals(password)) {
                return true;
            }
        }

        return false;
    }
}