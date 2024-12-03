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
import com.example.onlymovie.adapter.MovieAdapter;
import com.example.onlymovie.models.Cast;
import com.example.onlymovie.models.Movie;
import com.example.onlymovie.service.FavoriteService;
import com.example.onlymovie.service.ImageService;
import com.example.onlymovie.service.MovieService;
import com.example.onlymovie.utils.Enum;
import com.example.onlymovie.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MovieDetail extends AppCompatActivity {

    private TextView movieTitle, movieOverview, movieRuntime, movieVoteAverage;
    private Button backButton;
    private Long movieId;
    private ImageView movieImage;
    private ImageButton favoriteButton;

    private CreditAdapter creditAdapter;
    private MovieAdapter movieAdapter;

    private RecyclerView creditListView, movieRecommendationListView;

    private ArrayList<Cast> movieCasts = new ArrayList<>();
    private ArrayList<Movie> movieRecommendations = new ArrayList<>();

    private Boolean isFavorite = false;

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
        favoriteButton = findViewById(R.id.toggleFavoriteButton);
        creditListView = findViewById(R.id.creditRecyclerView);
        movieRecommendationListView = findViewById(R.id.movieRecommendationView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        creditListView.setLayoutManager(layoutManager);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        movieRecommendationListView.setLayoutManager(layoutManager2);

        creditAdapter = new CreditAdapter(this, movieCasts, new CreditAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Cast cast) {
                Intent intent = new Intent(getApplicationContext(), PeopleDetail.class);
                intent.putExtra(Enum.IntentValue.personId.name(), cast.getId());
                startActivity(intent);
            }
        });
        creditListView.setAdapter(creditAdapter);

        movieAdapter = new MovieAdapter(this, movieRecommendations, new MovieAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Movie movie) {
                Intent intent = new Intent(getApplicationContext(), MovieDetail.class);
                intent.putExtra(Enum.IntentValue.movieId.name(), movie.getId());
                startActivity(intent);
            }
        });
        movieRecommendationListView.setAdapter(movieAdapter);

        Intent intent = getIntent();
        movieId = intent.getLongExtra(Enum.IntentValue.movieId.name(), -1);

        if (movieId == -1) {
            Toast.makeText(getApplicationContext(), "Invalid Movie Id", Toast.LENGTH_SHORT).show();
        } else {
            fetchMovieById(movieId);
            fetchMovieCasts(movieId);
            fetchMovieRecommendations(movieId);
            checkFavoriteState(movieId);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFavorite = !isFavorite;

                if (isFavorite) {
                    favoriteButton.setImageResource(R.drawable.ic_heart_filled);
                    FavoriteService.addToFavorite(Enum.MEDIATYPE.Movie.name(), movieId);
                } else {
                    favoriteButton.setImageResource(R.drawable.ic_heart_empty);
                    FavoriteService.removeFromFavorite(Enum.MEDIATYPE.Movie.name(), movieId);
                }
            }
        });
    }


    private void checkFavoriteState(Long movieId) {
        FavoriteService.checkFavoriteState(Enum.MEDIATYPE.Movie.name(), movieId, new FavoriteService.CheckFavoriteCallback() {
            @Override
            public void onSuccess(Boolean isFavorite) {
                MovieDetail.this.isFavorite = isFavorite;
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

    private void fetchMovieById(Long movieId) {
        if (movieId == -1) {
            Toast.makeText(getApplicationContext(), "Invalid movie Id", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "Movie detail's not available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getApplicationContext(), "Failed to load movie details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchMovieCasts(Long movieId) {
        if (movieId == -1) {
            Toast.makeText(getApplicationContext(), "Invalid movie Id", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "Cast information's not available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getApplicationContext(), "Cast information's not available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchMovieRecommendations(Long movieId) {
        if (movieId == 0) {
            Toast.makeText(getApplicationContext(), "Invalid movie Id", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "Movie recommendation's not available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getApplicationContext(), "Movie recommendation's not available", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
