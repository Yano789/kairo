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

            Student authenticatedStudent = getAuthenticatedStudent(usernameInput, password);
            if (authenticatedStudent != null) {
                // Successful login - pass user email to MainActivity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("user_email", authenticatedStudent.getEmail());
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }
        private Student getAuthenticatedStudent (String usernameInput, String password){
            // Check if input is an ID (numeric)
            try {
                BigInteger inputId = new BigInteger(usernameInput);
                return checkById(inputId, password);
            } catch (NumberFormatException e) {
                // If not a number, treat as email/username
                return checkByEmail(usernameInput, password);
            }
        }

        private Student checkByEmail (String email, String password){
            ArrayList<Student> students = UserRepository.getSampleStudents();
            for (Student student : students) {
                if ((student.getEmail().equalsIgnoreCase(email) || student.getName().equalsIgnoreCase(email))
                        && student.getPassword().equals(password)) {
                    return student; // Return the student object instead of just true
                }
            }
            return null;
        }

        private Student checkById (BigInteger id, String password){
            ArrayList<Student> students = UserRepository.getSampleStudents();
            for (Student student : students) {
                if (student.getId().equals(id) && student.getPassword().equals(password)) {
                    return student; // Return the student object instead of just true
                }
            }
            return null;
        }
    }