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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlymovie.R;
import com.example.onlymovie.adapter.CreditAdapter;
import com.example.onlymovie.adapter.SeriesAdapter;
import com.example.onlymovie.models.Cast;
import com.example.onlymovie.models.Series;
import com.example.onlymovie.service.ImageService;
import com.example.onlymovie.service.SeriesService;

import java.util.ArrayList;
import java.util.List;

public class SeriesDetail extends AppCompatActivity {

    private TextView seriesName, seriesOverview, seriesVoteAverage;
    private ImageView seriesImage;
    private Long seriesId;
    private Button backButton;
    private RecyclerView creditRecyclerView, seriesRecommendationView;
    private SeriesAdapter seriesAdapter;
    private CreditAdapter creditAdapter;
    private ArrayList<Cast> seriesCasts = new ArrayList<>();
    private ArrayList<Series> seriesRecommendationsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_detail);

        seriesName = findViewById(R.id.seriesName);
        seriesOverview = findViewById(R.id.seriesOverview);
        seriesVoteAverage = findViewById(R.id.seriesVoteAverage);
        seriesImage = findViewById(R.id.seriesImage);
        backButton = findViewById(R.id.backButton);
        creditRecyclerView = findViewById(R.id.creditRecyclerView);
        seriesRecommendationView = findViewById(R.id.seriesRecommendationView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        creditRecyclerView.setLayoutManager(layoutManager);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        seriesRecommendationView.setLayoutManager(layoutManager2);

        creditAdapter = new CreditAdapter(this, seriesCasts, new CreditAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Cast cast) {
                Intent intent = new Intent(SeriesDetail.this, PeopleDetail.class);
                intent.putExtra("person-id", cast.getId());
                startActivity(intent);
            }
        });
        creditRecyclerView.setAdapter(creditAdapter);

        seriesAdapter = new SeriesAdapter(this, seriesRecommendationsList, new SeriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Series series) {
                Intent intent = new Intent(SeriesDetail.this, SeriesDetail.class);
                intent.putExtra("series-id", series.getId());
                startActivity(intent);
            }
        });

        seriesRecommendationView.setAdapter(seriesAdapter);


        Intent intent = getIntent();
        seriesId = intent.getLongExtra("series-id", -1);

        if (seriesId == -1) {
            seriesName.setText("Invalid Series Id");
        } else {
            fetchSeriesDetails(seriesId);
            fetchSeriesCasts(seriesId);
            fetchSeriesRecommendations(seriesId);
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
                seriesOverview.setText(series.getOverview());
                seriesVoteAverage.setText(String.format("Rating: %.1f", series.getVote_average()));

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

    private void fetchSeriesCasts(Long seriesId) {
        if (seriesId == 0) {
            seriesName.setText("Invalid Series Id");
            return;
        }

        SeriesService.fetchSeriesCredits(seriesId, new SeriesService.CreditServiceCallback() {
            @Override
            public void onSuccess(List<Cast> casts) {
                seriesCasts.clear();
                seriesCasts.addAll(casts);
                creditAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {
                seriesName.setText("Failed to load cast information");
            }
        });
    }

    private void fetchSeriesRecommendations(Long seriesId) {
        if (seriesId == 0) {
            seriesName.setText("Invalid series id");
            return;
        }

        SeriesService.fetchSeriesRecommendations(seriesId, new SeriesService.SeriesServiceCallback() {
            @Override
            public void onSuccess(List<Series> series) {
                seriesRecommendationsList.clear();
                seriesRecommendationsList.addAll(series);
                seriesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {
                seriesName.setText("Failed to load series recommendations");
            }
        });
    }
}