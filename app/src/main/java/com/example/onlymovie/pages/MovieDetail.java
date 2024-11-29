package com.example.onlymovie.pages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.example.onlymovie.utils.Utils;

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

        // Handle back button click
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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
                if (movie != null) {
                    String releaseYear = Utils.getYear(movie.getRelease_date());
                    String title = movie.getTitle();
                    String overview = movie.getOverview();
                    Double voteAverage = movie.getVote_average();
                    Long runtime = movie.getRuntime();
                    String posterPath = movie.getPoster_path();

                    movieTitle.setText(title != null ? title + " (" + releaseYear + ")" : "Untitled Movie");
                    movieOverview.setText(overview != null && !overview.isEmpty() ? overview : "Overview not available");
                    movieVoteAverage.setText(voteAverage != null ? String.format("%.1f", voteAverage) : "No rating");
                    movieRuntime.setText(runtime != null ? runtime + " minutes" : "Runtime not available");

                    if (posterPath != null && !posterPath.isEmpty()) {
                        ImageService.loadImage(posterPath, MovieDetail.this, movieImage);
                    } else {
                        movieImage.setImageResource(R.drawable.logo);
                        movieImage.setMinimumWidth(150);
                    }
                } else {
                    movieTitle.setText("Movie details not available.");
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
                if (castList != null && !castList.isEmpty()) {
                    movieCasts.clear();
                    movieCasts.addAll(castList);
                    creditAdapter.notifyDataSetChanged();
                } else {
                    movieCasts.clear();
                    creditAdapter.notifyDataSetChanged();
                    movieTitle.setText("No cast information available.");
                }
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
                if (movies != null && !movies.isEmpty()) {
                    movieRecommendations.clear();
                    movieRecommendations.addAll(movies);
                    movieAdapter.notifyDataSetChanged();
                } else {
                    movieRecommendations.clear();
                    movieAdapter.notifyDataSetChanged();
                    movieTitle.setText("No recommendations available.");
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                movieTitle.setText("Failed to load movie recommendations.");
            }
        });
    }
}
