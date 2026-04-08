package com.example.firebaseandroid.ui.booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.firebaseandroid.R;
import com.example.firebaseandroid.data.model.BookingState;
import com.example.firebaseandroid.data.model.Movie;
import com.example.firebaseandroid.data.model.Showtime;
import com.example.firebaseandroid.data.model.Theater;
import com.example.firebaseandroid.databinding.ActivityMovieDetailBinding;
import com.example.firebaseandroid.util.FormatUtils;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity {

    private ActivityMovieDetailBinding binding;
    private BookingViewModel viewModel;

    private Movie movie;
    private List<Theater> theaters = new ArrayList<>();
    private List<Showtime> showtimes = new ArrayList<>();
    private Theater selectedTheater;
    private Showtime selectedShowtime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMovieDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(BookingViewModel.class);

        loadMovieFromIntent();
        setupViews();
        loadTheatersAndShowtimes();
        observeViewModel();
    }

    private void loadMovieFromIntent() {
        movie = new Movie();
        movie.setMovieId(getIntent().getStringExtra("movieId"));
        movie.setTitle(getIntent().getStringExtra("movieTitle"));
        movie.setGenre(getIntent().getStringExtra("movieGenre"));
        movie.setDurationMinutes(getIntent().getIntExtra("movieDuration", 0));
        movie.setRating(getIntent().getDoubleExtra("movieRating", 0.0));
        movie.setDescription(getIntent().getStringExtra("movieDescription"));
        movie.setPosterUrl(getIntent().getStringExtra("moviePoster"));
    }

    private void setupViews() {
        binding.ivBack.setOnClickListener(v -> finish());
        binding.tvMovieTitle.setText(movie.getTitle());
        binding.tvGenre.setText(movie.getGenre());
        binding.tvDuration.setText(FormatUtils.formatDuration(movie.getDurationMinutes()));
        binding.tvRating.setText(FormatUtils.formatRating(movie.getRating()));
        binding.tvDescription.setText(movie.getDescription() != null ? movie.getDescription() : "");

        if (movie.getPosterUrl() != null && !movie.getPosterUrl().isEmpty()) {
            Glide.with(this)
                    .load(movie.getPosterUrl())
                    .placeholder(R.drawable.bg_movie_placeholder)
                    .into(binding.ivPoster);
        }

        binding.btnBookNow.setOnClickListener(v -> {
            if (selectedTheater == null) {
                binding.tvTheaterError.setVisibility(View.VISIBLE);
                return;
            }
            if (selectedShowtime == null) {
                binding.tvShowtimeError.setVisibility(View.VISIBLE);
                return;
            }

            BookingState bookingState = new BookingState();
            bookingState.setMovie(movie);
            bookingState.setTheater(selectedTheater);
            bookingState.setShowtime(selectedShowtime);

            Intent intent = new Intent(this, BookingSeatActivity.class);
            intent.putExtra("bookingState", bookingState);
            startActivity(intent);
        });
    }

    private void loadTheatersAndShowtimes() {
        viewModel.loadTheaters();
        viewModel.loadShowtimes(movie.getMovieId());
    }

    private void observeViewModel() {
        viewModel.getTheaters().observe(this, theaterList -> {
            if (theaterList != null) {
                theaters.clear();
                theaters.addAll(theaterList);
                populateTheaterSpinner();
            }
        });

        viewModel.getShowtimes().observe(this, showtimeList -> {
            if (showtimeList != null) {
                showtimes.clear();
                showtimes.addAll(showtimeList);
                populateShowtimeSpinner();
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }

    private void populateTheaterSpinner() {
        if (theaters.isEmpty()) {
            // Add sample theaters for demo
            Theater t1 = new Theater("th1", "Galaxy Cinema Nguyễn Trãi", "369 Nguyễn Trãi, Q5, TP.HCM", "TP.HCM");
            Theater t2 = new Theater("th2", "CGV Vincom Đồng Khởi", "72 Nguyễn Huệ, Q1, TP.HCM", "TP.HCM");
            Theater t3 = new Theater("th3", "Lotte Cinema Landmark 81", "Landmark 81, Bình Thạnh, TP.HCM", "TP.HCM");
            theaters.add(t1);
            theaters.add(t2);
            theaters.add(t3);
        }

        String[] names = new String[theaters.size()];
        for (int i = 0; i < theaters.size(); i++) {
            names[i] = theaters.get(i).getName();
        }

        binding.spinnerTheater.setSimpleItems(names);
        binding.spinnerTheater.setOnItemClickListener((adapterView, view, pos, id) -> {
            selectedTheater = theaters.get(pos);
            binding.tvTheaterError.setVisibility(View.GONE);
            filterShowtimesByTheater();
        });
    }

    private void filterShowtimesByTheater() {
        if (selectedTheater == null) return;

        List<Showtime> filtered = new ArrayList<>();
        for (Showtime s : showtimes) {
            if (s.getTheaterId() != null && s.getTheaterId().equals(selectedTheater.getTheaterId())) {
                filtered.add(s);
            }
        }

        if (filtered.isEmpty()) {
            // Add sample showtimes for demo
            filtered.add(createSampleShowtime("10:00", 60000));
            filtered.add(createSampleShowtime("13:30", 70000));
            filtered.add(createSampleShowtime("17:00", 70000));
            filtered.add(createSampleShowtime("20:30", 80000));
        }

        populateShowtimeSpinnerWithList(filtered);
    }

    private Showtime createSampleShowtime(String time, long price) {
        Showtime s = new Showtime();
        s.setShowtimeId("st_" + time.replace(":", ""));
        s.setMovieId(movie.getMovieId());
        s.setTheaterId(selectedTheater.getTheaterId());
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        s.setDate(sdf.format(new java.util.Date()));
        s.setTime(time);
        s.setTotalSeats(100);
        s.setAvailableSeats((int)(Math.random() * 50 + 30));
        s.setPrice(price);
        s.setRoomName("Room 1");
        return s;
    }

    private void populateShowtimeSpinner() {
        if (showtimes.isEmpty()) {
            binding.spinnerShowtime.setEnabled(false);
            return;
        }
        populateShowtimeSpinnerWithList(showtimes);
    }

    private void populateShowtimeSpinnerWithList(List<Showtime> list) {
        String[] times = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            Showtime s = list.get(i);
            times[i] = s.getTime() + " - " + FormatUtils.formatPrice(s.getPrice()) + " (" + s.getAvailableSeats() + " seats)";
        }
        binding.spinnerShowtime.setSimpleItems(times);
        binding.spinnerShowtime.setEnabled(list.size() > 0);
        binding.spinnerShowtime.setOnItemClickListener((adapterView, view, pos, id) -> {
            selectedShowtime = list.get(pos);
            binding.tvShowtimeError.setVisibility(View.GONE);
        });
    }
}
