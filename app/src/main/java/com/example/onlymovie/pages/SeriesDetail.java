package com.example.onlymovie.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.onlymovie.R;
import com.example.onlymovie.models.Series;
import com.example.onlymovie.service.ImageService;
import com.example.onlymovie.service.SeriesService;

public class SeriesDetail extends AppCompatActivity {

    private TextView seriesName;
    private ImageView seriesImage;
    private Long seriesId;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_detail);

        seriesName = findViewById(R.id.seriesName);
        seriesImage = findViewById(R.id.seriesImage);
        backButton = findViewById(R.id.backButton);


        Intent intent = getIntent();
        seriesId = intent.getLongExtra("series-id", -1);

        if (seriesId == -1) {
            seriesName.setText("Invalid Series Id");
        } else {
            fetchSeriesDetails(seriesId);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

    }

    private void fetchSeriesDetails(Long seriesId) {
        if (seriesId == 0) {
            seriesName.setText("Invalid Series Id");
            return;
        }

        SeriesService.fetchSeriesDetails(seriesId, new SeriesService.SeriesDetailCallback() {
            @Override
            public void onSuccess(Series series) {
                seriesName.setText(series.getName());

                String imageUrl = series.getPoster_path();

                if (!imageUrl.isEmpty() && imageUrl != null) {
                    ImageService.loadImage(imageUrl, SeriesDetail.this, seriesImage);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                seriesName.setText("Failed to fetch series details");
            }
        });
    }
}