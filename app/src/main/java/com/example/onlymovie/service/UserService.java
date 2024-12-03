package com.example.onlymovie.service;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.onlymovie.R;
import com.example.onlymovie.utils.Enum;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class UserService {

    private static FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public UserService() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public static String getCurrentUserId() {
        return mAuth.getCurrentUser().getUid();
    }

    public void loadProfileImage(Context context, String imageUrl, ImageView imageView) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context).load(imageUrl).into(imageView);
        } else {
            imageView.setImageResource(R.drawable.ic_launcher_background); // Fallback placeholder
        }
    }

    public void updateUsername(Context context, String newUsername) {
        if (newUsername.isEmpty()) {
            Toast.makeText(context, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        db.collection(Enum.FirebaseCollection.users.name()).whereEqualTo("username", newUsername)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        Toast.makeText(context, "Username already exists. Please choose another.", Toast.LENGTH_SHORT).show();
                    } else {
                        DocumentReference userDocRef = db.collection(Enum.FirebaseCollection.users.name()).document(userId);
                        userDocRef.update("username", newUsername)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(context, "Username updated successfully", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Failed to update username: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                });
    }

    public void updateFullname(Context context, String newFullname) {
        if (newFullname.isEmpty()) {
            Toast.makeText(context, "Full name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        db.collection(Enum.FirebaseCollection.users.name()).document(userId)
                .update("fullName", newFullname)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Full name updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to update full name: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    public void updateImageUrlInFirestore(Context context, String imageUrl) {
        String userId = mAuth.getCurrentUser().getUid();
        DocumentReference userDocRef = db.collection(Enum.FirebaseCollection.users.name()).document(userId);
        userDocRef.set(new HashMap<String, Object>() {{
            put("profileImageUrl", imageUrl);
        }}, SetOptions.merge()).addOnSuccessListener(aVoid -> {
            Toast.makeText(context, "Image URL updated successfully", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Failed to update image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    public void uploadImageToFirebaseStorage(Context context, Uri imageUri) {
        if (imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("uploads/" + System.currentTimeMillis() + ".jpg");
            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            updateImageUrlInFirestore(context, uri.toString());
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadUserProfileData(Context context, String userId, TextView profileFullname, TextView profileUsername, ImageView profileImageView) {
        db.collection(Enum.FirebaseCollection.users.name()).document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String username = task.getResult().getString("username");
                        String fullName = task.getResult().getString("fullName");
                        String imageUrl = task.getResult().getString("profileImageUrl");

                        profileUsername.setText(username);
                        profileFullname.setText(fullName);
                        loadProfileImage(context, imageUrl, profileImageView);
                    } else {
                        Toast.makeText(context, "Failed to load user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
