package com.example.onlymovie.service;

import com.example.onlymovie.models.Cast;
import com.example.onlymovie.models.Movie;
import com.example.onlymovie.models.SearchResult;
import com.example.onlymovie.response.CreditResponse;
import com.example.onlymovie.response.MovieResponse;
import com.example.onlymovie.response.SearchResponse;

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

    public static void fetchMovieById(Long movieId, final MovieDetailCallback callback) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<Movie> call = apiService.getMovieDetails(movieId, "d87f651a6b4efe803d9bb8e7b6cc5871");
        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("Error fetching movie details");
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                callback.onFailure("Error fetching movie details");
            }
        });
    }

    public static void fetchMovieCredits(Long movieId, final CreditServiceCallback callback) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<CreditResponse> call = apiService.getMovieCredits(movieId, "d87f651a6b4efe803d9bb8e7b6cc5871");

        call.enqueue(new Callback<CreditResponse>() {
            @Override
            public void onResponse(Call<CreditResponse> call, Response<CreditResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Cast> castList = response.body().getCast();

                    if (castList != null && castList.size() > 5) {
                        castList = castList.subList(0, 5);
                    }

                    callback.onSuccess(castList);
                } else {
                    callback.onFailure("Error fetching movie credits");
                }
            }

            @Override
            public void onFailure(Call<CreditResponse> call, Throwable t) {
                callback.onFailure("Error: " + t.getMessage());
            }
        });
    }

    public static void fetchMovieRecommendations(Long movieId, final MovieServiceCallback callback) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<MovieResponse> call = apiService.getMovieRecommendations(movieId, API_KEY);

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movieList = response.body().getResults();
                    callback.onSuccess(movieList);
                } else {
                    callback.onFailure("Error fetching movie recommendations");
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                callback.onFailure("Error fetching movie recommendations");
            }
        });
    }

    public static <T> void fetchSearchResult(String query, final SearchResultCallback callback) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<SearchResponse> call = apiService.getSearchMulti(query, API_KEY);

        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<SearchResult> results = response.body().getResults();
                    callback.onSuccess(results);
                } else {
                    callback.onFailure("No results found");
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public interface MovieServiceCallback {
        void onSuccess(List<Movie> movies);
        void onFailure(String errorMessage);
    }

    public interface MovieDetailCallback {
        void onSuccess(Movie movie);
        void onFailure(String errorMessage);
    }

    public interface CreditServiceCallback {
        void onSuccess(List<Cast> castList);
        void onFailure(String errorMessage);
    }

    public interface SearchResultCallback {
        void onSuccess(List<SearchResult> results);
        void onFailure(String errorMessage);
    }
}
