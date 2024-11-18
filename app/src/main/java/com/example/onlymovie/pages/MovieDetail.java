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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlymovie.R;
import com.example.onlymovie.adapter.CreditAdapter;
import com.example.onlymovie.adapter.MovieAdapter;
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

    private CreditAdapter creditAdapter;
    private MovieAdapter movieAdapter;
    private ArrayList<Cast> movieCasts = new ArrayList<>();
    private ArrayList<Movie> movieRecommendations = new ArrayList<>();

    private RecyclerView creditListView, movieRecommendationListView;

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
        creditListView = findViewById(R.id.creditRecyclerView);
        movieRecommendationListView = findViewById(R.id.movieRecommendationView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        creditListView.setLayoutManager(layoutManager);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        movieRecommendationListView.setLayoutManager(layoutManager2);

        creditAdapter = new CreditAdapter(this, movieCasts, new CreditAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Cast cast) {
                Intent intent = new Intent(MovieDetail.this, PeopleDetail.class);
                intent.putExtra("person-id", cast.getId());
                startActivity(intent);
            }
        });
        creditListView.setAdapter(creditAdapter);

        movieAdapter = new MovieAdapter(this, movieRecommendations, new MovieAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Movie movie) {
                Intent intent = new Intent(MovieDetail.this, MovieDetail.class);
                intent.putExtra("movie-id", movie.getId());
                startActivity(intent);
            }
        });

        movieRecommendationListView.setAdapter(movieAdapter);

        Intent intent = getIntent();
        movieId = intent.getLongExtra("movie-id", -1);

        if (movieId == -1) {
            movieTitle.setText("Invalid Movie ID");
        } else {
            fetchMovieById(movieId);
            fetchMovieCasts(movieId);
            fetchMovieRecommendations(movieId);
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
                Log.e("MovieDetail", "Failed to fetch movie credits: " + errorMessage);
                movieTitle.setText("Failed to load cast information.");
            }
        });
    }

    private void fetchMovieRecommendations(Long movieId) {
        if (movieId == 0) {
            movieTitle.setText("Invalid Movie Id");
            return;
        }

        MovieService.fetchMovieRecommendations(movieId, new MovieService.MovieServiceCallback() {
            @Override
            public void onSuccess(List<Movie> movies) {
                movieRecommendations.clear();
                movieRecommendations.addAll(movies);
                movieAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {
                movieTitle.setText("Failed to load movie recommendations.");
            }
        });
    }
}
