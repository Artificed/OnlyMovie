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
import com.example.onlymovie.models.People;

import java.util.ArrayList;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.PeopleViewHolder> {

    private Context context;
    private ArrayList<People> people;
    private OnItemClickListener listener;
    private static String baseImageUrl = "https://image.tmdb.org/t/p/w300/";

    public PeopleAdapter(Context context, ArrayList<People> people, OnItemClickListener listener) {
        this.context = context;
        this.people = (people != null) ? people : new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public PeopleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_credit, parent, false);
        return new PeopleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PeopleViewHolder holder, int position) {
        People person = people.get(position);
        holder.bind(person, listener);
    }

    @Override
    public int getItemCount() {
        return people.size();
    }

    public static class PeopleViewHolder extends RecyclerView.ViewHolder {
        private TextView actorName;
        private ImageView actorImage;

        public PeopleViewHolder(View itemView) {
            super(itemView);
            actorName = itemView.findViewById(R.id.movieActorText);
            actorImage = itemView.findViewById(R.id.movieActorImage);
        }

        public void bind(final People people, final OnItemClickListener listener) {
            actorName.setText(people.getName());

            String imageUrl = people.getProfile_path();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(baseImageUrl + imageUrl + "?api_key=d87f651a6b4efe803d9bb8e7b6cc5871")
                        .error(R.drawable.ic_launcher_background)
                        .into(actorImage);
            } else {
                actorImage.setImageResource(R.drawable.logo);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(people);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(People people);
    }
}

