package com.example.onlymovie;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class Profile extends AppCompatActivity {

    TextInputEditText editTextUsername, editTextFullname;
    Button editProfileButton, uploadImageButton;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    ProgressBar progressBar;
    TextView textView;
    private static final int PICK_IMAGE_REQUEST = 1;
    Uri imageUri;
    ImageView profileImageView;
    private ActivityResultLauncher<Intent> getImageLauncher;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            String userId = currentUser.getUid();
            db.collection("users").document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            String username = task.getResult().getString("username");
                            editTextUsername.setText(username);

                            String fullName = task.getResult().getString("fullName");
                            editTextFullname.setText(fullName);

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

        getImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                imageUri = result.getData().getData();
                profileImageView.setImageURI(imageUri);
                uploadImageToFirebaseStorage();
            }
        });

        editProfileButton.setOnClickListener(v -> {
            String newUsername = editTextUsername.getText().toString();
            if (!newUsername.isEmpty()) {
                updateUsername(newUsername);
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

    private void updateUsername(String newUsername) {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("users")
                    .whereEqualTo("username", newUsername)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(Profile.this, "Username already exists. Please choose another.", Toast.LENGTH_SHORT).show();
                        } else {
                            db.collection("users").document(userId)
                                    .update("username", newUsername)
                                    .addOnSuccessListener(aVoid -> {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(Profile.this, "Username updated successfully", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(Profile.this, "Failed to update username: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(Profile.this, "Error checking username: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void uploadImageToFirebaseStorage() {
        if (imageUri != null) {
            progressBar.setVisibility(View.VISIBLE);
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("uploads/" + System.currentTimeMillis() + ".jpg");

            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(Profile.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            updateImageUrlInFirestore(uri.toString());
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(Profile.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateImageUrlInFirestore(String imageUrl) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference userDocRef = db.collection("users").document(userId);
            userDocRef.set(
                    new HashMap() {{
                        put("profileImageUrl", imageUrl);
                    }},
                    SetOptions.merge()
            ).addOnSuccessListener(aVoid -> {
                Toast.makeText(Profile.this, "Image URL updated successfully", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(Profile.this, "Failed to update image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }


}