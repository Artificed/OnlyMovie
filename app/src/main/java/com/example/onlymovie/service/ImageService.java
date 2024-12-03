package com.example.onlymovie.service;

import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.example.onlymovie.R;

public class ImageService {

    private static String baseImageUrl = "https://image.tmdb.org/t/p/w300/";
    private static final String API_KEY = "d87f651a6b4efe803d9bb8e7b6cc5871";

    public static void loadImage(String imagePath, FragmentActivity activity, ImageView imageView) {
        if (imagePath != null && !imagePath.isEmpty()) {
            Glide.with(activity)
                    .load(baseImageUrl + imagePath + "?api_key=" + API_KEY)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.logo);
        }
    }
}
