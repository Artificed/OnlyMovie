package com.example.onlymovie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.onlymovie.R;
import com.example.onlymovie.models.Movie;

import java.util.ArrayList;

public class MovieAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Movie> movieList;

    private String baseImageUrl = "https://image.tmdb.org/t/p/w300/";

    private static final String API_KEY = "d87f651a6b4efe803d9bb8e7b6cc5871";

    public MovieAdapter(Context context, ArrayList<Movie> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @Override
    public int getCount() {
        return movieList.size();
    }

    @Override
    public Object getItem(int i) {
        return movieList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_movie, viewGroup, false);
        }

        TextView movieName = view.findViewById(R.id.movieTitle);
        ImageView movieImage = view.findViewById(R.id.movieImage);

        Movie movie = movieList.get(i);

        movieName.setText(movie.getTitle());

        String imageUrl = movie.getPoster_path();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(baseImageUrl + imageUrl + "?api_key=d87f651a6b4efe803d9bb8e7b6cc5871")
                    .error(R.drawable.ic_launcher_background)
                    .into(movieImage);
        } else {
            movieImage.setImageResource(R.drawable.ic_launcher_background);
        }

        return view;
    }
}
