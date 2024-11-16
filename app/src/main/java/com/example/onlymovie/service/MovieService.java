package com.example.onlymovie.service;

import com.example.onlymovie.models.Movie;
import com.example.onlymovie.response.MovieResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieService {
    private static final String API_KEY = "d87f651a6b4efe803d9bb8e7b6cc5871";

    public static void fetchTrendingMovies(final MovieServiceCallback callback) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<MovieResponse> call = apiService.getTrendingMovies(API_KEY);

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movieList = response.body().getResults();
                    callback.onSuccess(movieList);
                } else {
                    callback.onFailure("Error fetching movies");
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public interface MovieServiceCallback {
        void onSuccess(List<Movie> movieList);
        void onFailure(String errorMessage);
    }
}
