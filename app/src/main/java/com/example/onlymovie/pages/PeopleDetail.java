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

import com.example.onlymovie.R;
import com.example.onlymovie.models.People;
import com.example.onlymovie.service.ImageService;
import com.example.onlymovie.service.PeopleService;

public class PeopleDetail extends AppCompatActivity {

    private TextView actorName, actorBiography, actorBirthday, actorPopularity;
    private ImageView actorImage;
    private Button backButton;
    private Long personId;

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

        Intent intent = getIntent();
        personId = intent.getLongExtra("person-id", -1);

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
                actorBirthday.setText(people.getBirthday());
                actorPopularity.setText(String.format("Rating: %.1f", people.getPopularity()));

                if (!imageUrl.isEmpty() && imageUrl != null) {
                    ImageService.loadImage(imageUrl, PeopleDetail.this, actorImage);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                actorName.setText("Failed to load movie details.");
            }
        });
    }
}