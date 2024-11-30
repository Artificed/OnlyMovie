package com.example.onlymovie.pages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlymovie.R;
import com.example.onlymovie.adapter.MovieCreditAdapter;
import com.example.onlymovie.models.MovieCredit;
import com.example.onlymovie.models.People;
import com.example.onlymovie.service.FavoriteService;
import com.example.onlymovie.service.ImageService;
import com.example.onlymovie.service.PeopleService;
import com.example.onlymovie.utils.Enum;

import java.util.ArrayList;
import java.util.List;

public class PeopleDetail extends AppCompatActivity {

    private TextView actorName, actorBiography, actorBirthday, actorPopularity, personRole;
    private ImageView actorImage;
    private Button backButton;
    private Long personId;

    private MovieCreditAdapter movieCreditAdapter;
    private ArrayList<MovieCredit> movieCredits = new ArrayList<>();

    private RecyclerView personMovieCreditsView;
    private ImageButton favoriteButton;
    private Boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_detail);

        actorName = findViewById(R.id.actorName);
        actorImage = findViewById(R.id.actorImage);
        backButton = findViewById(R.id.backButton);
        favoriteButton = findViewById(R.id.toggleFavoriteButton);
        actorBiography = findViewById(R.id.actorBiography);
        actorBirthday = findViewById(R.id.actorBirthday);
        actorPopularity = findViewById(R.id.actorPopularity);
        personRole = findViewById(R.id.personRole);
        personMovieCreditsView = findViewById(R.id.personCombinedCredits);

        Intent intent = getIntent();
        personId = intent.getLongExtra("person-id", -1);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        personMovieCreditsView.setLayoutManager(layoutManager);

        movieCreditAdapter = new MovieCreditAdapter(this, movieCredits, new MovieCreditAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MovieCredit movie) {
                Intent intent = new Intent(PeopleDetail.this, MovieDetail.class);
                intent.putExtra("movie-id", movie.getId());
                startActivity(intent);
            }
        });
        personMovieCreditsView.setAdapter(movieCreditAdapter);

        if (personId == -1) {
            actorName.setText("Invalid person id");
        } else {
            fetchPeopleDetail(personId);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFavorite = !isFavorite;

                if (isFavorite) {
                    favoriteButton.setImageResource(R.drawable.ic_heart_filled);
                    FavoriteService.addToFavorite(Enum.MEDIATYPE.Person.name(), personId);
                } else {
                    favoriteButton.setImageResource(R.drawable.ic_heart_empty);
                    FavoriteService.removeFromFavorite(Enum.MEDIATYPE.Person.name(), personId);
                }
            }
        });
    }

    private void checkFavoriteState(Long personId) {
        FavoriteService.checkFavoriteState(Enum.MEDIATYPE.Person.name(), personId, new FavoriteService.CheckFavoriteCallback() {
            @Override
            public void onSuccess(Boolean isFavorite) {
                PeopleDetail.this.isFavorite = isFavorite;
                if (isFavorite) {
                    favoriteButton.setImageResource(R.drawable.ic_heart_filled);
                } else {
                    favoriteButton.setImageResource(R.drawable.ic_heart_empty);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("People Detail", "Error checking favorite state: " + errorMessage);
            }
        });
    }

    private void fetchPeopleDetail(Long personId) {
        if (personId == 0) {
            actorName.setText("Invalid person Id");
            return;
        }

        PeopleService.fetchPeopleDetail(personId, new PeopleService.PeopleDetailServiceCallback() {
            @Override
            public void onSuccess(People people) {
                actorName.setText(people.getName() != null ? people.getName() : "Name not available");

                String imageUrl = people.getProfile_path();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    ImageService.loadImage(imageUrl, PeopleDetail.this, actorImage);
                } else {
                    actorImage.setImageResource(R.drawable.logo);
                }

                actorBiography.setText(people.getBiography() != null ? people.getBiography() : "Biography not available");
                personRole.setText(people.getKnown_for_department() != null ? people.getKnown_for_department() : "Role not available");
                actorBirthday.setText(people.getBirthday() != null ? "Born: " + people.getBirthday() : "Birthday not available");
                actorPopularity.setText(people.getPopularity() != null ? String.format("Rating: %.1f", people.getPopularity()) : "Rating not available");

                if (people.getKnown_for_department() != null) {
                    if (people.getKnown_for_department().equals(Enum.KNOWN_FOR_DEPARTMENT.Acting.name())) {
                        fetchActorMovieCredit(personId);
                    } else {
                        fetchDirectorMovieCredit(personId);
                    }
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                actorName.setText("Failed to load person details.");
                actorBiography.setText("Error: " + errorMessage);
            }
        });
    }

    private void fetchActorMovieCredit(Long personId) {
        if (personId == 0) {
            actorName.setText("Invalid person Id");
            return;
        }

        PeopleService.fetchActorMovieCredit(personId, new PeopleService.ActorMovieCreditServiceCallback() {
            @Override
            public void onSuccess(List<MovieCredit> casts) {
                if (casts != null && !casts.isEmpty()) {
                    movieCredits.clear();
                    movieCredits.addAll(casts);
                    movieCreditAdapter.notifyDataSetChanged();
                } else {
                    movieCredits.clear();
                    movieCreditAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                actorName.setText("Failed to fetch person details");
            }
        });
    }

    private void fetchDirectorMovieCredit(Long personId) {
        if (personId == 0) {
            actorName.setText("Invalid person Id");
            return;
        }

        PeopleService.fetchDirectorMovieCredit(personId, new PeopleService.DirectorMovieCreditServiceCallback() {
            @Override
            public void onSuccess(List<MovieCredit> crew) {
                if (crew != null && !crew.isEmpty()) {
                    movieCredits.clear();
                    movieCredits.addAll(crew);
                    movieCreditAdapter.notifyDataSetChanged();
                } else {
                    movieCredits.clear();
                    movieCreditAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle failure to fetch credits
            }
        });
    }
}
