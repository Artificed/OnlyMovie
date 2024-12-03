package com.example.onlymovie.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlymovie.R;
import com.example.onlymovie.adapter.CreditAdapter;
import com.example.onlymovie.adapter.SeriesAdapter;
import com.example.onlymovie.models.Cast;
import com.example.onlymovie.models.Series;
import com.example.onlymovie.service.FavoriteService;
import com.example.onlymovie.service.ImageService;
import com.example.onlymovie.service.SeriesService;
import com.example.onlymovie.utils.Enum;

import java.util.ArrayList;
import java.util.List;

public class SeriesDetail extends AppCompatActivity {

    private TextView seriesName, seriesOverview, seriesVoteAverage;
    private ImageView seriesImage;
    private Long seriesId;
    private ImageButton favoriteButton;
    private Button backButton;

    private RecyclerView creditRecyclerView, seriesRecommendationView;
    private SeriesAdapter seriesAdapter;
    private CreditAdapter creditAdapter;

    private ArrayList<Cast> seriesCasts = new ArrayList<>();
    private ArrayList<Series> seriesRecommendationsList = new ArrayList<>();

    private Boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_detail);

        seriesName = findViewById(R.id.seriesName);
        seriesOverview = findViewById(R.id.seriesOverview);
        seriesVoteAverage = findViewById(R.id.seriesVoteAverage);
        seriesImage = findViewById(R.id.seriesImage);
        backButton = findViewById(R.id.backButton);
        favoriteButton = findViewById(R.id.toggleFavoriteButton);

        creditRecyclerView = findViewById(R.id.creditRecyclerView);
        seriesRecommendationView = findViewById(R.id.seriesRecommendationView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        creditRecyclerView.setLayoutManager(layoutManager);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        seriesRecommendationView.setLayoutManager(layoutManager2);

        creditAdapter = new CreditAdapter(this, seriesCasts, new CreditAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Cast cast) {
                Intent intent = new Intent(getApplicationContext(), PeopleDetail.class);
                intent.putExtra(Enum.IntentValue.personId.name(), cast.getId());
                startActivity(intent);
            }
        });
        creditRecyclerView.setAdapter(creditAdapter);

        seriesAdapter = new SeriesAdapter(this, seriesRecommendationsList, new SeriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Series series) {
                Intent intent = new Intent(getApplicationContext(), SeriesDetail.class);
                intent.putExtra(Enum.IntentValue.seriesId.name(), series.getId());
                startActivity(intent);
            }
        });
        seriesRecommendationView.setAdapter(seriesAdapter);

        Intent intent = getIntent();
        seriesId = intent.getLongExtra(Enum.IntentValue.seriesId.name(), -1);

        if (seriesId == -1) {
            Toast.makeText(getApplicationContext(), "Invalid Series Id", Toast.LENGTH_SHORT).show();
        } else {
            fetchSeriesDetails(seriesId);
            fetchSeriesCasts(seriesId);
            fetchSeriesRecommendations(seriesId);
            checkFavoriteState(seriesId);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFavorite = !isFavorite;

                if (isFavorite) {
                    favoriteButton.setImageResource(R.drawable.ic_heart_filled);
                    FavoriteService.addToFavorite(Enum.MEDIATYPE.Tv.name(), seriesId);
                } else {
                    favoriteButton.setImageResource(R.drawable.ic_heart_empty);
                    FavoriteService.removeFromFavorite(Enum.MEDIATYPE.Tv.name(), seriesId);
                }
            }
        });
    }

    private void checkFavoriteState(Long seriesId) {
        FavoriteService.checkFavoriteState(Enum.MEDIATYPE.Tv.name(), seriesId, new FavoriteService.CheckFavoriteCallback() {
            @Override
            public void onSuccess(Boolean isFavorite) {
                SeriesDetail.this.isFavorite = isFavorite;
                if (isFavorite) {
                    favoriteButton.setImageResource(R.drawable.ic_heart_filled);
                } else {
                    favoriteButton.setImageResource(R.drawable.ic_heart_empty);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getApplicationContext(), "Error checking favorite state", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchSeriesDetails(Long seriesId) {
        if (seriesId == -1) {
            Toast.makeText(getApplicationContext(), "Invalid Series Id", Toast.LENGTH_SHORT).show();
            return;
        }

        SeriesService.fetchSeriesDetails(seriesId, new SeriesService.SeriesDetailCallback() {
            @Override
            public void onSuccess(Series series) {
                if (series != null) {
                    String name = series.getName();
                    String overview = series.getOverview();
                    Double voteAverage = series.getVote_average();
                    String imageUrl = series.getPoster_path();

                    seriesName.setText(name != null && !name.isEmpty() ? name : "Unknown Series");
                    seriesOverview.setText(overview != null && !overview.isEmpty() ? overview : "Overview not available");
                    seriesVoteAverage.setText(voteAverage != null ? String.format("Rating: %.1f", voteAverage) : "No rating");

                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        ImageService.loadImage(imageUrl, SeriesDetail.this, seriesImage);
                    } else {
                        seriesImage.setImageResource(R.drawable.logo);
                        seriesImage.setMinimumWidth(150);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Series detail's not available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getApplicationContext(), "Failed to fetch series details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchSeriesCasts(Long seriesId) {
        if (seriesId == -1) {
            Toast.makeText(getApplicationContext(), "Invalid Series Id", Toast.LENGTH_SHORT).show();
            return;
        }

        SeriesService.fetchSeriesCredits(seriesId, new SeriesService.CreditServiceCallback() {
            @Override
            public void onSuccess(List<Cast> casts) {
                if (casts != null && !casts.isEmpty()) {
                    seriesCasts.clear();
                    seriesCasts.addAll(casts);
                    creditAdapter.notifyDataSetChanged();
                } else {
                    seriesCasts.clear();
                    creditAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), "Error fetching casts information", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getApplicationContext(), "Error fetching casts information", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchSeriesRecommendations(Long seriesId) {
        if (seriesId == -1) {
            Toast.makeText(getApplicationContext(), "Invalid Series Id", Toast.LENGTH_SHORT).show();
            return;
        }

        SeriesService.fetchSeriesRecommendations(seriesId, new SeriesService.SeriesServiceCallback() {
            @Override
            public void onSuccess(List<Series> series) {
                if (series != null && !series.isEmpty()) {
                    seriesRecommendationsList.clear();
                    seriesRecommendationsList.addAll(series);
                    seriesAdapter.notifyDataSetChanged();
                } else {
                    seriesRecommendationsList.clear();
                    seriesAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), "Series Recommendation's not available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getApplicationContext(), "Series Recommendation's not available", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
