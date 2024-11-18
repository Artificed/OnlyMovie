package com.example.onlymovie.service;

import com.example.onlymovie.models.Cast;
import com.example.onlymovie.models.People;
import com.example.onlymovie.response.PeopleResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PeopleService {
    private static final String API_KEY = "d87f651a6b4efe803d9bb8e7b6cc5871";

    public static void fetchTrendingPeople(final PeopleServiceCallback callback) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<PeopleResponse> call = apiService.getTrendingPeople(API_KEY);

        call.enqueue(new Callback<PeopleResponse>() {
            @Override
            public void onResponse(Call<PeopleResponse> call, Response<PeopleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Cast> castList = response.body().getResults();
                    callback.onSuccess(castList);
                } else {
                    callback.onFailure("Error fetching trending people");
                }
            }

            @Override
            public void onFailure(Call<PeopleResponse> call, Throwable t) {
                callback.onFailure("Error fetching trending people");
            }
        });
    }

    public static void fetchPeopleDetail(Long personId, final PeopleDetailServiceCallback callback) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<People> call = apiService.getPersonDetail(personId, API_KEY);

        call.enqueue(new Callback<People>() {
            @Override
            public void onResponse(Call<People> call, Response<People> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("Error fetching people detail");
                }
            }

            @Override
            public void onFailure(Call<People> call, Throwable t) {
                callback.onFailure("Error fetching people detail");
            }
        });
    }


    public interface PeopleServiceCallback {
        void onSuccess(List<Cast> casts);
        void onFailure(String errorMessage);
    }

    public interface PeopleDetailServiceCallback {
        void onSuccess(People people);
        void onFailure(String errorMessage);
    }

}

