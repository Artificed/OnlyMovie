package com.example.onlymovie.service;

import android.util.Log;

import com.example.onlymovie.models.FavoriteItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoriteService {
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public static void addToFavorite(String mediaType, Long id) {
        String userId = mAuth.getCurrentUser().getUid();

        Map<String, Object> favoriteData = new HashMap<>();
        favoriteData.put("id", id);
        favoriteData.put("timestamp", FieldValue.serverTimestamp());

        db.collection("favorites")
                .document(userId)
                .collection(mediaType)
                .document(String.valueOf(id))
                .set(favoriteData)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Added to favorites successfully");
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error adding to favorites: " + e.getMessage());
                });
    }

    public static void removeFromFavorite(String mediaType, Long id) {
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("favorites")
                .document(userId)
                .collection(mediaType)
                .document(String.valueOf(id))
                .delete()
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Removed from favorites successfully");
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error removing from favorites: " + e.getMessage());
                });
    }

    public static void checkFavoriteState(String mediaType, Long id, CheckFavoriteCallback callback) {
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("favorites")
                .document(userId)
                .collection(mediaType)
                .document(String.valueOf(id))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        callback.onSuccess(true);
                    } else {
                        callback.onSuccess(false);
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e.getMessage());
                });
    }

    public static void fetchAllFavorites(FetchAllFavoritesCallback callback) {

        String userId = mAuth.getCurrentUser().getUid();
        Log.d("FavoriteService", "Fetching favorites for user: " + userId);

        List<String> mediaTypes = Arrays.asList("Movie", "Tv", "Person");
        List<FavoriteItem> allFavorites = new ArrayList<>();

        final int[] queriesCompleted = {0};

        for (String mediaType : mediaTypes) {
            db.collection("favorites")
                    .document(userId)
                    .collection(mediaType)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                Long id = doc.getLong("id");
                                if (id != null) {
                                    allFavorites.add(new FavoriteItem(id, mediaType));
                                }
                            }
                            Log.d("FavoriteService", "Fetched " + queryDocumentSnapshots.size() + " " + mediaType + " favorites.");
                        }

                        queriesCompleted[0]++;
                        if (queriesCompleted[0] == mediaTypes.size()) {
                            Log.d("FavoriteService", "All queries finished, invoking callback.");
                            callback.onSuccess(allFavorites);
                        }
                    })
                    .addOnFailureListener(e -> {
                        callback.onFailure("Error fetching favorites: " + e.getMessage());
                    });
        }
    }



    public interface CheckFavoriteCallback {
        void onSuccess(Boolean isFavorite);
        void onFailure(String errorMessage);
    }

    public interface FetchAllFavoritesCallback {
        void onSuccess(List<FavoriteItem> favorites);
        void onFailure(String errorMessage);
    }
}
