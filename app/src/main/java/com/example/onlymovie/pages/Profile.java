package com.example.onlymovie.pages;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.onlymovie.R;
import com.example.onlymovie.service.UserService;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile extends AppCompatActivity {

    private TextInputEditText editTextUsername, editTextFullname;
    private Button editProfileButton, uploadImageButton;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private TextView profileFullname, profileUsername;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView profileImageView;
    private ActivityResultLauncher<Intent> getImageLauncher;
    private UserService userService;

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userService.loadUserProfileData(Profile.this, userId, profileFullname, profileUsername, profileImageView);

            db.collection("users").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            String username = documentSnapshot.getString("username");
                            String fullName = documentSnapshot.getString("fullName");

                            if (username != null) {
                                editTextUsername.setText(username);
                            }
                            if (fullName != null) {
                                editTextFullname.setText(fullName);
                            }

                            String imageUrl = task.getResult().getString("profileImageUrl");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Glide.with(Profile.this).load(imageUrl).into(profileImageView);
                            }

                        } else {
                            Toast.makeText(Profile.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        editTextFullname = findViewById(R.id.fullName);
        editTextUsername = findViewById(R.id.username);
        editProfileButton = findViewById(R.id.btnEditProfile);
        progressBar = findViewById(R.id.progressBar);
        uploadImageButton = findViewById(R.id.btnUploadImage);
        profileImageView = findViewById(R.id.profileImageView);
        profileFullname = findViewById(R.id.profileFullname);
        profileUsername = findViewById(R.id.profileUsername);

        userService = new UserService();

        getImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                imageUri = result.getData().getData();
                profileImageView.setImageURI(imageUri);
                userService.uploadImageToFirebaseStorage(Profile.this, imageUri);
            }
        });

        editProfileButton.setOnClickListener(v -> {
            String newUsername = editTextUsername.getText().toString();
            String newFullname = editTextFullname.getText().toString();

            editProfileButton.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);

            if (!newUsername.isEmpty() || !newFullname.isEmpty()) {
                if (!newUsername.isEmpty()) {
                    userService.updateUsername(Profile.this, newUsername);
                }

                if (!newFullname.isEmpty()) {
                    userService.updateFullname(Profile.this, newFullname);
                }

                if (!newUsername.isEmpty()) {
                    profileUsername.setText(newUsername);
                }
                if (!newFullname.isEmpty()) {
                    profileFullname.setText(newFullname);
                }



                new Handler().postDelayed(() -> {
                    editProfileButton.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                }, 5000);

                onStart();
            } else {
                Toast.makeText(Profile.this, "Please modify at least one field", Toast.LENGTH_SHORT).show();
                editProfileButton.setEnabled(true);
                progressBar.setVisibility(View.GONE);
            }
        });

        uploadImageButton.setOnClickListener(v -> openFileInput());
    }

    private void openFileInput() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        getImageLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }
}
