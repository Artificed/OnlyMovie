package com.example.onlymovie.pages;

import android.content.Intent;
import android.os.Bundle;


import android.widget.TextView;

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
                intent.putExtra("movie-id", movie.getId());
                startActivity(intent);
            }
        });
        movieListView.setAdapter(movieAdapter);


        seriesAdapter = new SeriesAdapter(this, seriesList, new SeriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Series series) {
                Intent intent = new Intent(Home.this, SeriesDetail.class);
                intent.putExtra("series-id", series.getId());
                startActivity(intent);
            }
        });
        seriesListView.setAdapter(seriesAdapter);

        creditAdapter = new CreditAdapter(this, peopleList, new CreditAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Cast cast) {
                Intent intent = new Intent(Home.this, PeopleDetail.class);
                intent.putExtra("person-id", cast.getId());
                startActivity(intent);
            }
        });
        peopleListView.setAdapter(creditAdapter);

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
                System.out.println("error");
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
                System.out.println("error");
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
                System.out.println("error");
            }
        });
    }
}