package com.example.onlymovie.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlymovie.R;
import com.example.onlymovie.adapter.MovieAdapter;
import com.example.onlymovie.adapter.PeopleAdapter;
import com.example.onlymovie.adapter.SeriesAdapter;
import com.example.onlymovie.models.Movie;
import com.example.onlymovie.models.People;
import com.example.onlymovie.models.Series;
import com.example.onlymovie.service.FavoriteService;
import com.example.onlymovie.service.MovieService;
import com.example.onlymovie.service.PeopleService;
import com.example.onlymovie.service.SeriesService;
import com.example.onlymovie.service.UserService;
import com.example.onlymovie.utils.Enum;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Profile extends AppCompatActivity {
    private TextView profileFullname, profileUsername;
    private ImageView profileImage;
    private Button editProfileButton;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private UserService userService;

    private RecyclerView favoriteMovieListView;
    private RecyclerView favoriteSeriesListView;
    private RecyclerView favoritePeopleListView;


    private MovieAdapter movieAdapter;
    private SeriesAdapter seriesAdapter;
    private PeopleAdapter peopleAdapter;


    private ArrayList<Movie> favoriteMovieList = new ArrayList<>();
    private ArrayList<Series> favoriteSeriesList = new ArrayList<>();
    private ArrayList<People> favoritePeopleList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileFullname = findViewById(R.id.profileFullname);
        profileUsername = findViewById(R.id.profileUsername);
        profileImage = findViewById(R.id.profileImageView);
        editProfileButton = findViewById(R.id.btnEditProfile);
        favoriteMovieListView = findViewById(R.id.favoriteMovieList);
        favoriteSeriesListView = findViewById(R.id.favoriteSeriesList);
        favoritePeopleListView = findViewById(R.id.favoritePeopleList);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        favoriteMovieListView.setLayoutManager(layoutManager);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        favoriteSeriesListView.setLayoutManager(layoutManager2);

        LinearLayoutManager layoutManager3 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        favoritePeopleListView.setLayoutManager(layoutManager3);


        movieAdapter = new MovieAdapter(this, favoriteMovieList, new MovieAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Movie movie) {
                Intent intent = new Intent(getApplicationContext(), MovieDetail.class);
                intent.putExtra("movie-id", movie.getId());
                startActivity(intent);
            }
        });

        favoriteMovieListView.setAdapter(movieAdapter);

        seriesAdapter = new SeriesAdapter(this, favoriteSeriesList, new SeriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Series series) {
                Intent intent = new Intent(getApplicationContext(), SeriesDetail.class);
                intent.putExtra("series-id", series.getId());
                startActivity(intent);
            }
        });
        favoriteSeriesListView.setAdapter(seriesAdapter);

        peopleAdapter = new PeopleAdapter(this, favoritePeopleList, new PeopleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(People people) {
                Intent intent = new Intent(getApplicationContext(), PeopleDetail.class);
                intent.putExtra("person-id", people.getId());
                startActivity(intent);
            }
        });
        favoritePeopleListView.setAdapter(peopleAdapter);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        userService = new UserService();

        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userService.loadUserProfileData(Profile.this, userId, profileFullname, profileUsername, profileImage);

            db.collection("users").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            String username = documentSnapshot.getString("username");
                            String fullName = documentSnapshot.getString("fullName");

                            if (username != null) {
                                profileFullname.setText(username);
                            }
                            if (fullName != null) {
                                profileFullname.setText(fullName);
                            }

                            String imageUrl = task.getResult().getString("profileImageUrl");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Glide.with(Profile.this).load(imageUrl).into(profileImage);
                            }

                        } else {
                            Toast.makeText(Profile.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditProfile.class);
                startActivity(intent);
                finish();
            }
        });

        fetchFavoriteMovie();
        fetchFavoriteSeries();
        fetchFavoritePeople();
    }


    private void fetchFavoriteMovie() {
        FavoriteService.fetchFavoriteByMediaType(Enum.MEDIATYPE.Movie.name(), new FavoriteService.FetchByMediaTypeCallback() {
            @Override
            public void onSuccess(List<Long> favorites) {
                List<Movie> moviesToAdd = new ArrayList<>();
                final int totalMovies = favorites.size();
                final int[] fetchedMoviesCount = {0};

                for (Long favorite : favorites) {
                    MovieService.fetchMovieById(favorite, new MovieService.MovieDetailCallback() {
                        @Override
                        public void onSuccess(Movie movie) {
                            if (movie != null) {
                                moviesToAdd.add(movie);
                            }

                            fetchedMoviesCount[0]++;

                            if (fetchedMoviesCount[0] == totalMovies) {
                                favoriteMovieList.clear();
                                favoriteMovieList.addAll(moviesToAdd);

                                movieAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Toast.makeText(Profile.this, "Error fetching movie lists", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(Profile.this, "Error fetching movie lists", Toast.LENGTH_SHORT).show();

            }
        });
    };

    private void fetchFavoriteSeries() {
        FavoriteService.fetchFavoriteByMediaType(Enum.MEDIATYPE.Tv.name(), new FavoriteService.FetchByMediaTypeCallback() {
            @Override
            public void onSuccess(List<Long> favorites) {
                List<Series> seriesToAdd = new ArrayList<>();
                final int totalSeries = favorites.size();
                final int[] fetchedSeriesCount = {0};

                for (Long favorite : favorites) {
                    SeriesService.fetchSeriesDetails(favorite, new SeriesService.SeriesDetailCallback() {
                        @Override
                        public void onSuccess(Series series) {
                            if (series != null) {
                                seriesToAdd.add(series);
                            }

                            fetchedSeriesCount[0]++;

                            if (fetchedSeriesCount[0] == totalSeries) {
                                favoriteSeriesList.clear();
                                favoriteSeriesList.addAll(seriesToAdd);

                                seriesAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Toast.makeText(Profile.this, "Error fetching series lists", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(Profile.this, "Error fetching series lists", Toast.LENGTH_SHORT).show();
            }
        });
    };


    private void fetchFavoritePeople() {
        FavoriteService.fetchFavoriteByMediaType(Enum.MEDIATYPE.Person.name(), new FavoriteService.FetchByMediaTypeCallback() {
            @Override
            public void onSuccess(List<Long> favorites) {
                List<People> peopleToAdd = new ArrayList<>();
                final int totalPeople = favorites.size();
                final int[] fetchedPeopleCount = {0};

                for (Long favorite : favorites) {
                    PeopleService.fetchPeopleDetail(favorite, new PeopleService.PeopleDetailServiceCallback() {
                        @Override
                        public void onSuccess(People people) {
                            if (people != null) {
                                peopleToAdd.add(people);
                            }

                            fetchedPeopleCount[0]++;

                            if (fetchedPeopleCount[0] == totalPeople) {
                                favoritePeopleList.clear();
                                favoritePeopleList.addAll(peopleToAdd);

                                peopleAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Toast.makeText(Profile.this, "Error fetching people lists", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(Profile.this, "Error fetching people lists", Toast.LENGTH_SHORT).show();
            }
        });
    };
}