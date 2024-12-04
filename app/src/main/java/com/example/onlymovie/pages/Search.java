package com.example.onlymovie.pages;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlymovie.R;
import com.example.onlymovie.adapter.SearchAdapter;
import com.example.onlymovie.models.SearchResult;
import com.example.onlymovie.service.MovieService;
import com.example.onlymovie.utils.Enum;

import java.util.ArrayList;
import java.util.List;

public class Search extends AppCompatActivity {

    private EditText editTextSearch;
    private SearchAdapter searchAdapter;

    private ArrayList<SearchResult> searchResults = new ArrayList<>();

    private RecyclerView searchResultView;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private TextView displayText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        editTextSearch = findViewById(R.id.editTextSearch);
        searchResultView = findViewById(R.id.searchResultView);
        displayText = findViewById(R.id.displayText);
        displayText.setText("");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        searchResultView.setLayoutManager(layoutManager);

        searchAdapter = new SearchAdapter(this, searchResults, new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SearchResult result) {
                if ("movie".equals(result.getMedia_type())) {
                    Intent intent = new Intent(getApplicationContext(), MovieDetail.class);
                    intent.putExtra(Enum.IntentValue.movieId.name(), result.getId());
                    startActivity(intent);
                } else if ("tv".equals(result.getMedia_type())) {
                    Intent intent = new Intent(getApplicationContext(), SeriesDetail.class);
                    intent.putExtra(Enum.IntentValue.seriesId.name(), result.getId());
                    startActivity(intent);
                } else if ("person".equals(result.getMedia_type())) {
                    Intent intent = new Intent(getApplicationContext(), PeopleDetail.class);
                    intent.putExtra(Enum.IntentValue.personId.name(), result.getId());
                    startActivity(intent);
                }

            }
        });
        searchResultView.setAdapter(searchAdapter);

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                handler.removeCallbacks(searchRunnable);

                searchRunnable = new Runnable() {
                    @Override
                    public void run() {
                        String query = charSequence.toString().trim();
                        if (!query.isEmpty()) {
                            fetchSearchResult(query);
                        } else {
                            searchResults.clear();
                            searchAdapter.notifyDataSetChanged();
                            displayText.setText("");
                        }
                    }
                };

                handler.postDelayed(searchRunnable, 500);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }

    private void fetchSearchResult(String query) {
        MovieService.fetchSearchResult(query, new MovieService.SearchResultCallback() {
            @Override
            public void onSuccess(List<SearchResult> results) {
                searchResults.clear();
                searchResults.addAll(results);
                searchAdapter.notifyDataSetChanged();

                if (!results.isEmpty()) {
                    displayText.setText("Searching \"" + query + "\", Results: " + results.size());
                } else {
                    displayText.setText("Searching \"" + query + "\", no results found.");
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                displayText.setText("Searching \"" + query + "\", failed to load results.");
            }
        });
    }

}