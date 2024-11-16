package com.example.onlymovie.service;


import com.example.onlymovie.models.Movie;
import com.example.onlymovie.response.MovieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("trending/movie/day")
    Call<MovieResponse> getTrendingMovies(@Query("api_key") String apiKey);

    @GET("movie/{movie_id}")
    Call<Movie> getMovieDetails(@Path("movie_id") Long movieId, @Query("api_key") String apiKey);
}


