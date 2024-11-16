package com.example.onlymovie.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.onlymovie.R;
import com.example.onlymovie.models.Movie;
import com.example.onlymovie.service.ApiClient;
import com.example.onlymovie.service.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetail extends AppCompatActivity {

    private TextView movieTitle, movieOverview, movieRuntime, movieVoteAverage;
    private Button backButton;
    private Long movieId;
    private ImageView movieImage;

    private String baseImageUrl = "https://image.tmdb.org/t/p/w300/";
    private static final String API_KEY = "d87f651a6b4efe803d9bb8e7b6cc5871";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        movieImage = findViewById(R.id.movieImage);
        movieTitle = findViewById(R.id.movieTitle);
        movieOverview = findViewById(R.id.movieOverview);
        movieRuntime = findViewById(R.id.movieRuntime);
        movieVoteAverage = findViewById(R.id.movieVoteAverage);


        backButton = findViewById(R.id.backButton);

        Intent intent = getIntent();
        movieId = intent.getLongExtra("movie-id", -1);

        if (movieId == -1) {
            movieTitle.setText("Invalid Movie ID");
        } else {
            fetchMovieById(movieId);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

    private void fetchMovieById(Long movieId) {
        if (movieId == 0) {
            movieTitle.setText("Invalid Movie ID");
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Movie> call = apiService.getMovieDetails(movieId, API_KEY);
        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Movie movie = response.body();
                    movieTitle.setText(movie.getTitle());

                    String imageUrl = movie.getPoster_path();
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(MovieDetail.this)
                                .load(baseImageUrl + imageUrl + "?api_key=d87f651a6b4efe803d9bb8e7b6cc5871")
                                .into(movieImage);
                    } else {
                        movieImage.setImageResource(R.drawable.ic_launcher_background);  // Fallback image
                    }

                    movieOverview.setText(movie.getOverview());

                    if (movie.getRuntime() != null) {
                        movieRuntime.setText(movie.getRuntime() + " minutes");
                    } else {
                        movieRuntime.setText("N/A");
                    }

                    if (movie.getVote_average() != null) {
                        movieVoteAverage.setText(String.format("Rating: %.1f", movie.getVote_average()));
                    } else {
                        movieVoteAverage.setText("N/A");
                    }

                } else {
                    movieTitle.setText("Failed to load movie details.");
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                movieTitle.setText("Failed to load movie details.");
            }
        });
    }
}
