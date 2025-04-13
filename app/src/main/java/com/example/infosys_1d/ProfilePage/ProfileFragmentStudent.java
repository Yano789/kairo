package com.example.infosys_1d.ProfilePage;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.infosys_1d.Login.Student;
import com.example.infosys_1d.Login.UserRepository;
import com.example.infosys_1d.R;

import java.math.BigInteger;
import java.util.ArrayList;

public class ProfileFragmentStudent extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView background;
    private ImageView profileImage;
    private ImageView imagePicker;
    private TextView greetingTextView;
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView idTextView;
    private TextView pillarTextView;
    private EditText genderEditText;
    private EditText birthdayEditText;
    private EditText linkedinEditText;
    private EditText addressEditText;
    private EditText phoneEditText;

    private boolean isProfileImageSelected = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_student, container, false);

        // Initialize all views
        background = view.findViewById(R.id.background);
        profileImage = view.findViewById(R.id.profileImage);
        imagePicker = view.findViewById(R.id.ImagePicker);
        greetingTextView = view.findViewById(R.id.greetingTextView);

        // Initialize TextViews by finding their direct IDs
        // Add these IDs to your TextViews in XML if they don't exist
        nameTextView = view.findViewById(R.id.nameTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        idTextView = view.findViewById(R.id.idTextView);
        pillarTextView = view.findViewById(R.id.pillarTextView);

        // Initialize EditText fields
        genderEditText = view.findViewById(R.id.genderEditText);
        birthdayEditText = view.findViewById(R.id.birthdayEditText);
        linkedinEditText = view.findViewById(R.id.linkedinEditText);
        addressEditText = view.findViewById(R.id.addressEditText);
        phoneEditText = view.findViewById(R.id.phoneEditText);

        // Get the logged-in user's email from intent
        String userEmail = requireActivity().getIntent().getStringExtra("user_email");

        if (userEmail != null) {
            Student currentStudent = getStudentByEmail(userEmail);
            if (currentStudent != null) {
                populateStudentData(currentStudent);
            } else {
                showToast("User data not found");
            }
        } else {
            showToast("No user email found");
        }

        // Set click listeners
        setClickListeners();
        return view;
    }

    private void setClickListeners() {
        background.setOnClickListener(v -> {
            isProfileImageSelected = false;
            openImagePicker();
        });

        profileImage.setOnClickListener(v -> {
            isProfileImageSelected = true;
            openImagePicker();
        });

        imagePicker.setOnClickListener(v -> {
            isProfileImageSelected = true;
            openImagePicker();
        });
    }

    private Student getStudentByEmail(String email) {
        if (email == null) return null;

        ArrayList<Student> students = UserRepository.getSampleStudents();
        for (Student student : students) {
            if (student.getEmail().equalsIgnoreCase(email)) {
                return student;
            }
        }
        return null;
    }

    private void populateStudentData(Student student) {
        if (student == null) return;

        // Set greeting with first name
        String firstName = student.getName().split(" ")[0];
        setTextSafe(greetingTextView, String.format("Hello, %s!", firstName));

        // Set basic information
        setTextSafe(nameTextView, student.getName());
        setTextSafe(emailTextView, student.getEmail());
        setTextSafe(idTextView, student.getId().toString());
        setTextSafe(pillarTextView, student.getFacultyName() != null ?
                student.getFacultyName() : "Course not specified");

        // Set hints for additional info
        setHintSafe(genderEditText, "Add Gender");
        setHintSafe(birthdayEditText, "Add Birthday");
        setHintSafe(linkedinEditText, "Add LinkedIn Profile");
        setHintSafe(addressEditText, "Add Mailing Address");
        setHintSafe(phoneEditText, "Add Phone Number");
    }

    private void setTextSafe(TextView textView, String text) {
        if (textView != null) {
            textView.setText(text);
        }
    }

    private void setHintSafe(EditText editText, String hint) {
        if (editText != null) {
            editText.setHint(hint);
        }
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            if (isProfileImageSelected) {
                profileImage.setImageURI(imageUri);
            } else {
                background.setImageURI(imageUri);
            }
        }
    }
}