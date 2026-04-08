package com.example.firebaseandroid.ui.main;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.firebaseandroid.R;
import com.example.firebaseandroid.data.repository.MovieRepository;
import com.example.firebaseandroid.data.repository.NotificationRepository;
import com.example.firebaseandroid.data.repository.TheaterRepository;
import com.example.firebaseandroid.databinding.ActivityMainBinding;
import com.example.firebaseandroid.ui.home.HomeFragment;
import com.example.firebaseandroid.ui.profile.ProfileFragment;
import com.example.firebaseandroid.ui.ticket.TicketListFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                // Permission result handled - we proceed regardless
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MovieRepository movieRepo = MovieRepository.getInstance();
        TheaterRepository theaterRepo = TheaterRepository.getInstance();

        // Use SharedPreferences to ensure seeding only happens once
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        if (!prefs.getBoolean("is_data_seeded", false)) {
            // 1. Seed movies first (using auto-generated Firebase IDs)
            movieRepo.seedSampleMovies();

            // 2. Load movies and observe the results
            movieRepo.loadNowShowingMovies();
            movieRepo.getMovies().observe(this, movies -> {
                if (movies != null && !movies.isEmpty()) {
                    // 3. Once movies are loaded, seed theaters and showtimes using real Movie IDs
                    theaterRepo.seedSampleTheaters(movies);
                    // Mark as seeded
                    prefs.edit().putBoolean("is_data_seeded", true).apply();
                }
            });
        }

        requestNotificationPermission();
        setupBottomNavigation();

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        // Subscribe to FCM general topic
        NotificationRepository.getInstance(this).subscribeToGeneralTopic();
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                fragment = new HomeFragment();
            } else if (itemId == R.id.nav_tickets) {
                fragment = new TicketListFragment();
            } else if (itemId == R.id.nav_profile) {
                fragment = new ProfileFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
