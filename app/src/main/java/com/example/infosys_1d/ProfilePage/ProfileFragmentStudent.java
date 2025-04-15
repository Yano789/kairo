package com.example.infosys_1d.ProfilePage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.infosys_1d.Login.Student;
import com.example.infosys_1d.Login.UserRepository;
import com.example.infosys_1d.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class ProfileFragmentStudent extends Fragment {

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

    // Activity result launcher for photo picker
    private final androidx.activity.result.ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        try {
                            Bitmap bitmap = loadBitmapFromUri(imageUri, isProfileImageSelected);
                            if (isProfileImageSelected) {
                                profileImage.setImageBitmap(bitmap);
                                saveBitmap("profile_image", bitmap);
                            } else {
                                background.setImageBitmap(bitmap);
                                saveBitmap("background_image", bitmap);
                            }
                        } catch (Exception e) {
                            showToast("Failed to load image: " + e.getMessage());
                        }
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

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

    private void saveBitmap(String key, Bitmap bitmap) {
        if (userEmail == null || bitmap == null) return;
        try {
            File file = new File(requireContext().getFilesDir(), userEmail + "_" + key + ".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos); // Use JPEG, 85% quality
            fos.close();
        } catch (Exception e) {
            showToast("Failed to save image: " + e.getMessage());
        }
    }

    private Bitmap loadBitmap(String key) {
        if (userEmail == null) return null;
        try {
            File file = new File(requireContext().getFilesDir(), userEmail + "_" + key + ".jpg");
            if (file.exists()) {
                return BitmapFactory.decodeFile(file.getAbsolutePath());
            }
        } catch (Exception e) {
            showToast("Failed to load saved image");
        }
        return null;
    }

    private void loadSavedData() {
        if (userEmail == null) return;

        // Load EditText data
        setTextSafe(genderEditText, sharedPreferences.getString(userEmail + "_gender", ""));
        setTextSafe(birthdayEditText, sharedPreferences.getString(userEmail + "_birthday", ""));
        setTextSafe(linkedinEditText, sharedPreferences.getString(userEmail + "_linkedin", ""));
        setTextSafe(addressEditText, sharedPreferences.getString(userEmail + "_address", ""));
        setTextSafe(phoneEditText, sharedPreferences.getString(userEmail + "_phone", ""));

        // Load ImageView data
        Bitmap profileBitmap = loadBitmap("profile_image");
        if (profileBitmap != null) {
            profileImage.setImageBitmap(profileBitmap);
        }

        Bitmap backgroundBitmap = loadBitmap("background_image");
        if (backgroundBitmap != null) {
            background.setImageBitmap(backgroundBitmap);
        }
    }

    private Bitmap loadBitmapFromUri(Uri uri, boolean isProfile) throws Exception {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
        BitmapFactory.decodeStream(inputStream, null, options);
        if (inputStream != null) {
            inputStream.close();
        }

        // Set target dimensions
        int targetWidth = isProfile ? 130 : 1080; // Profile: 130dp, Background: max 1080px width
        int targetHeight = isProfile ? 130 : 1920; // Profile: 130dp, Background: max 1920px height

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight);
        options.inJustDecodeBounds = false;

        // Decode scaled bitmap
        inputStream = requireContext().getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        if (inputStream != null) {
            inputStream.close();
        }

        if (bitmap == null) {
            throw new Exception("Failed to decode bitmap");
        }

        return bitmap;
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
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
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        imagePickerLauncher.launch(intent);
    }
}