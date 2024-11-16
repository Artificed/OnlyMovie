package com.example.onlymovie.pages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.onlymovie.R;
import com.example.onlymovie.adapter.CreditAdapter;
import com.example.onlymovie.models.Cast;
import com.example.onlymovie.models.Movie;
import com.example.onlymovie.service.ImageService;
import com.example.onlymovie.service.MovieService;

import java.util.ArrayList;
import java.util.List;

public class MovieDetail extends AppCompatActivity {

    private TextView movieTitle, movieOverview, movieRuntime, movieVoteAverage;
    private Button backButton;
    private Long movieId;
    private ImageView movieImage;

    private String baseImageUrl = "https://image.tmdb.org/t/p/w300/";
    private static final String API_KEY = "d87f651a6b4efe803d9bb8e7b6cc5871";
    private CreditAdapter creditAdapter;
    private ArrayList<Cast> movieCasts = new ArrayList<>();

    private ListView creditListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Area Linking XML Component to Java
        movieImage = findViewById(R.id.movieImage);
        movieTitle = findViewById(R.id.movieTitle);
        movieOverview = findViewById(R.id.movieOverview);
        movieRuntime = findViewById(R.id.movieRuntime);
        movieVoteAverage = findViewById(R.id.movieVoteAverage);
        backButton = findViewById(R.id.backButton);
        creditListView = findViewById(R.id.creditListView);

        // Area Setup Credit Adapter (For Actors)
        creditAdapter = new CreditAdapter(this, movieCasts);
        creditListView.setAdapter(creditAdapter);

        // Receiving intent from home
        Intent intent = getIntent();
        movieId = intent.getLongExtra("movie-id", -1);

        if (movieId == -1) {
            movieTitle.setText("Invalid Movie ID");
        } else {
            fetchMovieById(movieId);
            fetchMovieCasts(movieId);
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

        MovieService.fetchMovieById(movieId, new MovieService.MovieDetailCallback() {
            @Override
            public void onSuccess(Movie movie) {
                movieTitle.setText(movie.getTitle());
                movieOverview.setText(movie.getOverview());
                movieVoteAverage.setText(String.format("Rating: %.1f", movie.getVote_average()));
                movieRuntime.setText(movie.getRuntime() + " minutes");
                String imageUrl = movie.getPoster_path();

                if (!imageUrl.isEmpty() && imageUrl != null) {
                    ImageService.loadImage(imageUrl, MovieDetail.this, movieImage);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                movieTitle.setText("Failed to load movie details.");
            }
        });
    }

    private void fetchMovieCasts(Long movieId) {
        if (movieId == 0) {
            movieTitle.setText("Invalid Movie ID");
            return;
        }

        MovieService.fetchMovieCredits(movieId, new MovieService.CreditServiceCallback() {
            @Override
            public void onSuccess(List<Cast> castList) {
                movieCasts.clear();
                movieCasts.addAll(castList);
                creditAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {
                // Log and show the failure message to the user
                Log.e("MovieDetail", "Failed to fetch movie credits: " + errorMessage);
                movieTitle.setText("Failed to load cast information.");
            }
        });

    }
}
