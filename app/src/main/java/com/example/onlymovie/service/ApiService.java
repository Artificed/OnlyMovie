package com.example.onlymovie.service;


import com.example.onlymovie.models.Cast;
import com.example.onlymovie.models.Movie;
import com.example.onlymovie.models.People;
import com.example.onlymovie.models.Series;
import com.example.onlymovie.response.CreditResponse;
import com.example.onlymovie.response.MovieResponse;
import com.example.onlymovie.response.PeopleResponse;
import com.example.onlymovie.response.SeriesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("trending/movie/day")
    Call<MovieResponse> getTrendingMovies(@Query("api_key") String apiKey);

    @GET("movie/{movie_id}")
    Call<Movie> getMovieDetails(@Path("movie_id") Long movieId, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/credits")
    Call<CreditResponse> getMovieCredits(@Path("movie_id") Long movieId, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/recommendations")
    Call<MovieResponse> getMovieRecommendations(@Path("movie_id") Long movieId, @Query("api_key") String apiKey);

    @GET("trending/tv/day")
    Call<SeriesResponse> getTrendingSeries(@Query("api_key") String apiKey);

    @GET("tv/{series_id}")
    Call<Series> getSeriesDetails(@Path("series_id") Long seriesId, @Query("api_key") String apiKey);

    @GET("tv/{series_id}/credits")
    Call<CreditResponse> getSeriesCredits(@Path("series_id") Long seriesId, @Query("api_key") String apiKey);

    @GET("tv/{series_id}/recommendations")
    Call<SeriesResponse> getSeriesRecommendations(@Path("series_id") Long seriesId, @Query("api_key") String apiKey);

    @GET("trending/person/day")
    Call<PeopleResponse> getTrendingPeople(@Query("api_key") String apiKey);

    @GET("person/{person_id}")
    Call<People> getPersonDetail(@Path("person_id") Long personId, @Query("api_key") String apiKey);
}


