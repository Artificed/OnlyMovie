package com.example.onlymovie.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlymovie.R;
import com.example.onlymovie.models.Series;

import java.util.List;

public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.SeriesViewHolder> {

    private Context context;
    private List<Series> seriesList;
    private OnItemClickListener listener;
    private static String baseImageUrl = "https://image.tmdb.org/t/p/w300/";

    public SeriesAdapter(Context context, List<Series> seriesList, OnItemClickListener listener) {
        this.context = context;
        this.seriesList = seriesList;
        this.listener = listener;
    }

    @Override
    public SeriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_series, parent, false);
        return new SeriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SeriesViewHolder holder, int position) {
        Series series = seriesList.get(position);
        holder.bind(series, listener);
    }

    @Override
    public int getItemCount() {
        return seriesList.size();
    }

    public static class SeriesViewHolder extends RecyclerView.ViewHolder {
        private TextView seriesName;
        private ImageView seriesImage;

        public SeriesViewHolder(View itemView) {
            super(itemView);
            seriesName = itemView.findViewById(R.id.seriesName);
            seriesImage = itemView.findViewById(R.id.seriesImage);
        }

        public void bind(final Series series, final OnItemClickListener listener) {
            seriesName.setText(series.getName());

            String imageUrl = series.getPoster_path();

            if (!imageUrl.isEmpty() && imageUrl != null) {
                Glide.with(itemView.getContext())
                        .load(baseImageUrl + imageUrl + "?api_key=d87f651a6b4efe803d9bb8e7b6cc5871")
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(seriesImage);
            } else {
                seriesImage.setImageResource(R.drawable.ic_launcher_background);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(series);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Series series);
    }
}
