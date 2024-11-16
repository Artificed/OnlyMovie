package com.example.onlymovie.pages;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.onlymovie.R;
import com.example.onlymovie.adapter.MovieAdapter;
import com.example.onlymovie.models.Movie;
import com.example.onlymovie.models.Series;
import com.example.onlymovie.response.MovieResponse;
import com.example.onlymovie.service.ApiClient;
import com.example.onlymovie.service.ApiService;
import com.example.onlymovie.service.MovieService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Home extends AppCompatActivity {

    private TextView titleTrending;
    private ListView listView;
    private ArrayList<Movie> movieList = new ArrayList<>();
    private ArrayList<Series> seriesList = new ArrayList<>();
    private MovieAdapter movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        titleTrending = findViewById(R.id.titleHomeTrending);
        listView = findViewById(R.id.listView);

        movieAdapter = new MovieAdapter(this, movieList);
        listView.setAdapter(movieAdapter);

        fetchMovies();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Movie selectedMovie = movieList.get(i);

                Intent intent = new Intent(Home.this, MovieDetail.class);
                intent.putExtra("movie-id", selectedMovie.getId());
                startActivity(intent);
            }
        });
    }

    private void fetchMovies() {
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
    }
}