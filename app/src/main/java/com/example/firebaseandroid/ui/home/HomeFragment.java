package com.example.firebaseandroid.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.firebaseandroid.data.model.Movie;
import com.example.firebaseandroid.databinding.FragmentHomeBinding;
import com.example.firebaseandroid.ui.booking.MovieDetailActivity;

public class HomeFragment extends Fragment implements MovieAdapter.OnMovieClickListener {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private MovieAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        setupRecyclerView();
        setupSearch();
        observeViewModel();

        viewModel.loadMovies();
    }

    private void setupRecyclerView() {
        adapter = new MovieAdapter(this);
        binding.recyclerViewMovies.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerViewMovies.setAdapter(adapter);
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.searchMovies(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void observeViewModel() {
        viewModel.getMovies().observe(getViewLifecycleOwner(), movies -> {
            if (movies != null && !movies.isEmpty()) {
                adapter.submitList(movies);
                binding.tvNoMovies.setVisibility(View.GONE);
                binding.recyclerViewMovies.setVisibility(View.VISIBLE);
            } else {
                binding.tvNoMovies.setVisibility(View.VISIBLE);
                binding.recyclerViewMovies.setVisibility(View.GONE);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                binding.tvNoMovies.setText(error);
                binding.tvNoMovies.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getSearchResults().observe(getViewLifecycleOwner(), results -> {
            if (results != null) {
                adapter.submitList(results);
                binding.tvNoMovies.setVisibility(results.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onMovieClick(Movie movie) {
        Intent intent = new Intent(getContext(), MovieDetailActivity.class);
        intent.putExtra("movieId", movie.getMovieId());
        intent.putExtra("movieTitle", movie.getTitle());
        intent.putExtra("movieGenre", movie.getGenre());
        intent.putExtra("movieDuration", movie.getDurationMinutes());
        intent.putExtra("movieRating", movie.getRating());
        intent.putExtra("movieDescription", movie.getDescription());
        intent.putExtra("moviePoster", movie.getPosterUrl());
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
