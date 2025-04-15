package com.example.infosys_1d.ProfilePage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.infosys_1d.R;

import java.io.InputStream;

public class ProfileFragmentOrg extends Fragment {

    private ImageView background;
    private ImageView profileImage;

    private boolean isProfileImageSelected = false;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

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
                            } else {
                                background.setImageBitmap(bitmap);
                            }
                        } catch (Exception e) {
                            showToast("Failed to load image: " + e.getMessage());
                        }
                    }
                }
            });

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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        background = view.findViewById(R.id.background);
        profileImage = view.findViewById(R.id.profileImage);

        // Set click listeners
        background.setOnClickListener(v -> {
            isProfileImageSelected = false;
            openImagePicker();
        });

        profileImage.setOnClickListener(v -> {
            isProfileImageSelected = true;
            openImagePicker();
        });

        return view;
    }

    private void openImagePicker() {
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        imagePickerLauncher.launch(intent);
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

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}