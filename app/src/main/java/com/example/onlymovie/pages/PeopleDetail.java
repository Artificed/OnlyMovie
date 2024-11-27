package com.example.onlymovie.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlymovie.R;
import com.example.onlymovie.adapter.MovieAdapter;
import com.example.onlymovie.adapter.MovieCreditAdapter;
import com.example.onlymovie.models.Movie;
import com.example.onlymovie.models.MovieCredit;
import com.example.onlymovie.models.People;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_detail);

        actorName = findViewById(R.id.actorName);
        actorImage = findViewById(R.id.actorImage);
        backButton = findViewById(R.id.backButton);
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
    }

    private void fetchPeopleDetail(Long personId) {
        if (personId == 0) {
            actorName.setText("Invalid person Id");
            return;
        }


        PeopleService.fetchPeopleDetail(personId, new PeopleService.PeopleDetailServiceCallback() {
            @Override
            public void onSuccess(People people) {
                actorName.setText(people.getName());
                String imageUrl = people.getProfile_path();
                actorBiography.setText(people.getBiography());
                personRole.setText(people.getKnown_for_department());
                actorBirthday.setText("Born: " + people.getBirthday());
                actorPopularity.setText(String.format("Rating: %.1f", people.getPopularity()));

                if (!imageUrl.isEmpty() && imageUrl != null) {
                    ImageService.loadImage(imageUrl, PeopleDetail.this, actorImage);
                }

                if (people.getKnown_for_department().equals(Enum.KNOWN_FOR_DEPARTMENT.Acting.name())) {
                    fetchActorMovieCredit(personId);
                }
                else {
                    fetchDirectorMovieCredit(personId);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                actorName.setText("Failed to load movie details.");
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
                movieCredits.clear();
                movieCredits.addAll(casts);
                movieCreditAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {

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
                movieCredits.clear();
                movieCredits.addAll(crew);
                movieCreditAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }
}