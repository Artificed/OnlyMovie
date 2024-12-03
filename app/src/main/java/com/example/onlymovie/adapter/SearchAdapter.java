package com.example.onlymovie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlymovie.R;
import com.example.onlymovie.models.SearchResult;
import com.example.onlymovie.utils.Enum;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private Context context;
    private List<SearchResult> searchList;
    private SearchAdapter.OnItemClickListener listener;
    private static String baseImageUrl = "https://image.tmdb.org/t/p/w500/";

    public SearchAdapter(Context context, List<SearchResult> searchList, SearchAdapter.OnItemClickListener listener) {
        this.context = context;
        this.searchList = searchList;
        this.listener = listener;
    }

    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_search, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchAdapter.SearchViewHolder holder, int position) {
        SearchResult search = searchList.get(position);
        holder.bind(search, listener);
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        private TextView searchTitle, searchCategory;
        private ImageView searchImage;

        public SearchViewHolder(View itemView) {
            super(itemView);
            searchTitle = itemView.findViewById(R.id.searchResultName);
            searchImage = itemView.findViewById(R.id.searchResultImage);
            searchCategory = itemView.findViewById(R.id.searchResultCategory);
        }

        public void bind(final SearchResult result, final SearchAdapter.OnItemClickListener listener) {
            if (result.getMedia_type().equals(Enum.SearchResultType.movie.name())) {
                searchTitle.setText(result.getTitle());
                searchCategory.setText(Enum.MEDIATYPE.Movie.name());
            } else if (result.getMedia_type().equals(Enum.SearchResultType.tv.name())) {
                searchTitle.setText(result.getName());
                searchCategory.setText(Enum.MEDIATYPE.Tv.name());
            } else if (result.getMedia_type().equals(Enum.SearchResultType.person.name())) {
                searchTitle.setText(result.getName());
                if( result.getKnown_for_department().equals(Enum.KNOWN_FOR_DEPARTMENT.Acting.name())) {
                    searchCategory.setText(Enum.JOB.Actor.name());
                } else {
                    searchCategory.setText(Enum.JOB.Director.name());
                }
            }

            String imageUrl = result.getPoster_path();
            if (imageUrl == null || imageUrl.isEmpty()) {
                imageUrl = result.getProfile_path();
            }

            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(baseImageUrl + imageUrl + "?api_key=d87f651a6b4efe803d9bb8e7b6cc5871")
                        .placeholder(R.drawable.baseline_person_24)
                        .error(R.drawable.baseline_person_24)
                        .into(searchImage);
            } else {
                searchImage.setImageResource(R.drawable.logo);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(result);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(SearchResult result);
    }
}
