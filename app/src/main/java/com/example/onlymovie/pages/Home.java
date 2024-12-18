package com.example.onlymovie.pages;

import android.content.Intent;
import android.os.Bundle;


import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlymovie.R;
import com.example.onlymovie.adapter.CreditAdapter;
import com.example.onlymovie.adapter.MovieAdapter;
import com.example.onlymovie.adapter.SeriesAdapter;
import com.example.onlymovie.models.Cast;
import com.example.onlymovie.models.Movie;
import com.example.onlymovie.models.Series;
import com.example.onlymovie.service.MovieService;
import com.example.onlymovie.service.PeopleService;
import com.example.onlymovie.service.SeriesService;
import com.example.onlymovie.utils.Enum;

import java.util.ArrayList;
import java.util.List;


public class Home extends AppCompatActivity {

    private TextView titleTrendingMovies, titleTrendingSeries, titleTrendingPeople;
    private RecyclerView movieListView, seriesListView, peopleListView;

    private ArrayList<Movie> movieList = new ArrayList<>();
    private ArrayList<Series> seriesList = new ArrayList<>();
    private ArrayList<Cast> peopleList = new ArrayList<>();

    private MovieAdapter movieAdapter;
    private SeriesAdapter seriesAdapter;
    private CreditAdapter creditAdapter;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        titleTrendingMovies = findViewById(R.id.titleMovieTrending);
        movieListView = findViewById(R.id.trendingMovieList);

        titleTrendingSeries = findViewById(R.id.titleSeriesTrending);
        seriesListView = findViewById(R.id.trendingSeriesList);

        titleTrendingPeople = findViewById(R.id.titlePeopleTrending);
        peopleListView = findViewById(R.id.trendingPeopleList);

        backButton = findViewById(R.id.backButton);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        movieListView.setLayoutManager(layoutManager);
        seriesListView.setLayoutManager(layoutManager2);
        peopleListView.setLayoutManager(layoutManager3);

        movieAdapter = new MovieAdapter(this, movieList, new MovieAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Movie movie) {
                Intent intent = new Intent(Home.this, MovieDetail.class);
                intent.putExtra(Enum.IntentValue.movieId.name(), movie.getId());
                startActivity(intent);
            }
        });
        movieListView.setAdapter(movieAdapter);


        seriesAdapter = new SeriesAdapter(this, seriesList, new SeriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Series series) {
                Intent intent = new Intent(Home.this, SeriesDetail.class);
                intent.putExtra(Enum.IntentValue.seriesId.name(), series.getId());
                startActivity(intent);
            }
        });
        seriesListView.setAdapter(seriesAdapter);

        creditAdapter = new CreditAdapter(this, peopleList, new CreditAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Cast cast) {
                Intent intent = new Intent(Home.this, PeopleDetail.class);
                intent.putExtra(Enum.IntentValue.personId.name(), cast.getId());
                startActivity(intent);
            }
        });
        peopleListView.setAdapter(creditAdapter);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        fetchingData();
    }

    private void fetchingData() {
        MovieService.fetchTrendingMovies(new MovieService.MovieServiceCallback() {
            @Override
            public void onSuccess(List<Movie> movies) {
                movieList.clear();
                movieList.addAll(movies);
                movieAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(Home.this, "Error fetching movie lists", Toast.LENGTH_SHORT).show();
            }
        });

        SeriesService.fetchTrendingSeries(new SeriesService.SeriesServiceCallback() {
            @Override
            public void onSuccess(List<Series> series) {
                seriesList.clear();
                seriesList.addAll(series);
                seriesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(Home.this, "Error fetching series lists", Toast.LENGTH_SHORT).show();
            }
        });

        PeopleService.fetchTrendingPeople(new PeopleService.PeopleServiceCallback() {
            @Override
            public void onSuccess(List<Cast> casts) {
                peopleList.clear();
                peopleList.addAll(casts);
                creditAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(Home.this, "Error fetching people lists", Toast.LENGTH_SHORT).show();
            }
        });
    }
}