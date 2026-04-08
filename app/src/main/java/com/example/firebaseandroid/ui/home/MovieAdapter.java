package com.example.firebaseandroid.ui.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.firebaseandroid.R;
import com.example.firebaseandroid.data.model.Movie;
import com.example.firebaseandroid.databinding.ItemMovieBinding;
import com.example.firebaseandroid.util.FormatUtils;

public class MovieAdapter extends ListAdapter<Movie, MovieAdapter.MovieViewHolder> {

    private final OnMovieClickListener listener;

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

    public MovieAdapter(OnMovieClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Movie> DIFF_CALLBACK = new DiffUtil.ItemCallback<Movie>() {
        @Override
        public boolean areItemsTheSame(@NonNull Movie oldItem, @NonNull Movie newItem) {
            return oldItem.getMovieId() != null && oldItem.getMovieId().equals(newItem.getMovieId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Movie oldItem, @NonNull Movie newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                   oldItem.getRating() == newItem.getRating();
        }
    };

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMovieBinding binding = ItemMovieBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new MovieViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        private final ItemMovieBinding binding;

        MovieViewHolder(ItemMovieBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Movie movie) {
            binding.tvMovieTitle.setText(movie.getTitle());
            binding.tvGenre.setText(movie.getGenre());
            binding.tvRating.setText(FormatUtils.formatRating(movie.getRating()));
            binding.tvDuration.setText(FormatUtils.formatDuration(movie.getDurationMinutes()));

            // Load poster image with Glide
            if (movie.getPosterUrl() != null && !movie.getPosterUrl().isEmpty()) {
                Glide.with(binding.getRoot().getContext())
                        .load(movie.getPosterUrl())
                        .placeholder(R.drawable.bg_movie_placeholder)
                        .error(R.drawable.bg_movie_placeholder)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .centerCrop()
                        .into(binding.ivPoster);
            } else {
                binding.ivPoster.setImageResource(R.drawable.bg_movie_placeholder);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMovieClick(movie);
                }
            });
        }
    }
}
