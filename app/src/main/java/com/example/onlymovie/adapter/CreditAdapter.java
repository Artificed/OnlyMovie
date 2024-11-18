package com.example.onlymovie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlymovie.R;
import com.example.onlymovie.models.Cast;

import java.util.ArrayList;

public class CreditAdapter extends RecyclerView.Adapter<CreditAdapter.CreditViewHolder> {

    private Context context;
    private ArrayList<Cast> casts;
    private OnItemClickListener listener;
    private static String baseImageUrl = "https://image.tmdb.org/t/p/w300/";

    public CreditAdapter(Context context, ArrayList<Cast> casts, OnItemClickListener listener) {
        this.context = context;
        this.casts = (casts != null) ? casts : new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public CreditViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_credit, parent, false);
        return new CreditViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CreditViewHolder holder, int position) {
        Cast cast = casts.get(position);
        holder.bind(cast, listener);
    }

    @Override
    public int getItemCount() {
        return casts.size();
    }

    public static class CreditViewHolder extends RecyclerView.ViewHolder {
        private TextView actorName;
        private ImageView actorImage;

        public CreditViewHolder(View itemView) {
            super(itemView);
            actorName = itemView.findViewById(R.id.movieActorText);
            actorImage = itemView.findViewById(R.id.movieActorImage);
        }

        public void bind(final Cast cast, final OnItemClickListener listener) {
            actorName.setText(cast.getName());

            String imageUrl = cast.getProfile_path();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(baseImageUrl + imageUrl + "?api_key=d87f651a6b4efe803d9bb8e7b6cc5871")
                        .error(R.drawable.ic_launcher_background)  // Placeholder if the image fails to load
                        .into(actorImage);
            } else {
                actorImage.setImageResource(R.drawable.ic_launcher_background);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(cast);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Cast cast);
    }
}

