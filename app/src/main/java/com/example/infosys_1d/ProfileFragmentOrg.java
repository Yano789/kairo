package com.example.infosys_1d;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ProfileFragmentOrg extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView backgroundImage;
    private ImageView profileImage;

    private boolean isProfileImageSelected = false;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ProfileFragmentOrg() {}

    public static ProfileFragmentOrg newInstance(String param1, String param2) {
        ProfileFragmentOrg fragment = new ProfileFragmentOrg();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_student, container, false);

        backgroundImage = view.findViewById(R.id.backgroundImage);
        profileImage = view.findViewById(R.id.profileImage);

        // when background image is clicked
        backgroundImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isProfileImageSelected = false;
                openImagePicker();
            }
        });

        // when profile image is clicked
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isProfileImageSelected = true;
                openImagePicker();
            }
        });

        return view;
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
                backgroundImage.setImageURI(imageUri);
            }
        }
    }
}

