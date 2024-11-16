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
import com.example.onlymovie.models.Cast;

import java.util.ArrayList;

public class CreditAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Cast> casts;

    private String baseImageUrl = "https://image.tmdb.org/t/p/w300/";

    public CreditAdapter(Context context, ArrayList<Cast> casts) {
        this.context = context;
        this.casts = casts;
    }

    @Override
    public int getCount() {
        return casts.size();
    }

    @Override
    public Object getItem(int i) {
        return casts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_credit, viewGroup, false);
        }

        TextView actorName = view.findViewById(R.id.movieActorText);
        ImageView actorImage = view.findViewById(R.id.movieActorImage);

        Cast castMember = casts.get(i);

        actorName.setText(castMember.getName());

        String imageUrl = castMember.getProfile_path();

        if (!imageUrl.isEmpty() && imageUrl != null) {
            Glide.with(context)
                    .load(baseImageUrl + imageUrl + "?api_key=d87f651a6b4efe803d9bb8e7b6cc5871")
                    .error(R.drawable.ic_launcher_background)
                    .into(actorImage);
        } else {
            actorImage.setImageResource(R.drawable.ic_launcher_background);
        }

        return view;
    }
}
