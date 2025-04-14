package com.example.infosys_1d.ProfilePage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "StudentProfilePrefs";
    private String userEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_student, container, false);

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Initialize all views
        background = view.findViewById(R.id.background);
        profileImage = view.findViewById(R.id.profileImage);
        imagePicker = view.findViewById(R.id.ImagePicker);
        greetingTextView = view.findViewById(R.id.greetingTextView);

        // Initialize TextViews
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
        userEmail = requireActivity().getIntent().getStringExtra("user_email");

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

        // Load saved EditText and ImageView data
        loadSavedData();

        // Set click listeners
        setClickListeners();

        // Set TextWatchers to save EditText changes
        setTextWatchers();

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

    private void setTextWatchers() {
        genderEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                saveData("gender", s.toString());
            }
        });

        birthdayEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                saveData("birthday", s.toString());
            }
        });

        linkedinEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                saveData("linkedin", s.toString());
            }
        });

        addressEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                saveData("address", s.toString());
            }
        });

        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                saveData("phone", s.toString());
            }
        });
    }

    private void saveData(String key, String value) {
        if (userEmail == null) return;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(userEmail + "_" + key, value);
        editor.apply();
    }

    private void saveImageUri(String key, Uri uri) {
        if (userEmail == null) return;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(userEmail + "_" + key, uri != null ? uri.toString() : "");
        editor.apply();
    }

    private void loadSavedData() {
        if (userEmail == null) return;

        // Load EditText data
        setTextSafe(genderEditText, sharedPreferences.getString(userEmail + "_gender", ""));
        setTextSafe(birthdayEditText, sharedPreferences.getString(userEmail + "_birthday", ""));
        setTextSafe(linkedinEditText, sharedPreferences.getString(userEmail + "_linkedin", ""));
        setTextSafe(addressEditText, sharedPreferences.getString(userEmail + "_address", ""));
        setTextSafe(phoneEditText, sharedPreferences.getString(userEmail + "_phone", ""));

        // Load ImageView URIs
        String profileImageUriString = sharedPreferences.getString(userEmail + "_profile_image_uri", "");
        if (!profileImageUriString.isEmpty()) {
            try {
                Uri profileImageUri = Uri.parse(profileImageUriString);
                profileImage.setImageURI(profileImageUri);
            } catch (Exception e) {
                showToast("Failed to load profile image");
            }
        }

        String backgroundImageUriString = sharedPreferences.getString(userEmail + "_background_image_uri", "");
        if (!backgroundImageUriString.isEmpty()) {
            try {
                Uri backgroundImageUri = Uri.parse(backgroundImageUriString);
                background.setImageURI(backgroundImageUri);
            } catch (Exception e) {
                showToast("Failed to load background image");
            }
        }
    }

    private void setTextSafe(EditText editText, String text) {
        if (editText != null && text != null && !text.isEmpty()) {
            editText.setText(text);
        }
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

        // Set hints for additional info (only if no saved data)
        if (sharedPreferences.getString(userEmail + "_gender", "").isEmpty()) {
            setHintSafe(genderEditText, "Add Gender");
        }
        if (sharedPreferences.getString(userEmail + "_birthday", "").isEmpty()) {
            setHintSafe(birthdayEditText, "Add Birthday");
        }
        if (sharedPreferences.getString(userEmail + "_linkedin", "").isEmpty()) {
            setHintSafe(linkedinEditText, "Add LinkedIn Profile");
        }
        if (sharedPreferences.getString(userEmail + "_address", "").isEmpty()) {
            setHintSafe(addressEditText, "Add Mailing Address");
        }
        if (sharedPreferences.getString(userEmail + "_phone", "").isEmpty()) {
            setHintSafe(phoneEditText, "Add Phone Number");
        }
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
                saveImageUri("profile_image_uri", imageUri);
            } else {
                background.setImageURI(imageUri);
                saveImageUri("background_image_uri", imageUri);
            }
        }
    }
}