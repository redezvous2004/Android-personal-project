package com.example.firebaseandroid.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.firebaseandroid.data.model.Movie;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MovieRepository {

    private static MovieRepository instance;
    private final FirebaseFirestore firestore;
    private final MutableLiveData<List<Movie>> movies = new MutableLiveData<>();
    private final MutableLiveData<List<Movie>> searchResults = new MutableLiveData<>();
    private final MutableLiveData<Movie> selectedMovie = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private MovieRepository() {
        firestore = FirebaseFirestore.getInstance();
    }

    public static synchronized MovieRepository getInstance() {
        if (instance == null) {
            instance = new MovieRepository();
        }
        return instance;
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }

    public LiveData<List<Movie>> getSearchResults() {
        return searchResults;
    }

    public LiveData<Movie> getSelectedMovie() {
        return selectedMovie;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    /**
     * Load all now showing movies
     */
    public void loadNowShowingMovies() {
        isLoading.postValue(true);
        error.postValue(null);

        firestore.collection("movies")
                .whereEqualTo("nowShowing", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    isLoading.postValue(false);
                    if (task.isSuccessful()) {
                        List<Movie> list = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Movie movie = doc.toObject(Movie.class);
                            movie.setMovieId(doc.getId());
                            list.add(movie);
                        }
                        movies.postValue(list);
                    } else {
                        error.postValue("Failed to load movies: " +
                                (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                    }
                });
    }

    /**
     * Search movies by title
     */
    public void searchMovies(String query) {
        if (query == null || query.trim().isEmpty()) {
            searchResults.postValue(new ArrayList<>());
            return;
        }

        isLoading.postValue(true);
        String searchQuery = query.trim().toLowerCase();

        firestore.collection("movies")
                .whereEqualTo("nowShowing", true)
                .orderBy("title")
                .get()
                .addOnCompleteListener(task -> {
                    isLoading.postValue(false);
                    if (task.isSuccessful()) {
                        List<Movie> results = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Movie movie = doc.toObject(Movie.class);
                            movie.setMovieId(doc.getId());
                            String title = movie.getTitle() != null ? movie.getTitle().toLowerCase() : "";
                            if (title.contains(searchQuery)) {
                                results.add(movie);
                            }
                        }
                        searchResults.postValue(results);
                    }
                });
    }

    /**
     * Get movie by ID
     */
    public void getMovieById(String movieId, OnCompleteListener<QuerySnapshot> callback) {
        firestore.collection("movies")
                .document(movieId)
                .collection("info")
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        Movie movie = task.getResult().getDocuments().get(0).toObject(Movie.class);
                        if (movie != null) {
                            movie.setMovieId(movieId);
                            selectedMovie.postValue(movie);
                        }
                    }
                    if (callback != null) callback.onComplete(task);
                });
    }

    /**
     * Seed sample movies into Firestore (for demo purposes)
     * Call this once to populate initial data
     */
    public void seedSampleMovies() {
        String[][] sampleData = {
            {"Avengers: Endgame", "Action, Sci-Fi", "181", "The Avengers assemble once more to reverse Thanos' actions...", "9.2", "Anthony Russo"},
            {"Spider-Man: No Way Home", "Action, Adventure", "148", "Peter Parker's identity is revealed...", "8.6", "Jon Watts"},
            {"The Batman", "Action, Crime", "176", "When a sadistic serial killer murders the elite of Gotham...", "8.4", "Matt Reeves"},
            {"Top Gun: Maverick", "Action, Drama", "131", "After thirty years, Maverick is still pushing the envelope...", "8.7", "Joseph Kosinski"},
            {"Black Panther: Wakanda Forever", "Action, Drama", "161", "The Wakandans fight to protect their nation...", "7.8", "Ryan Coogler"},
        };

        for (String[] data : sampleData) {
            Movie movie = new Movie();
            // Use auto-generated ID from Firestore
            movie.setMovieId(firestore.collection("movies").document().getId());
            movie.setTitle(data[0]);
            movie.setGenre(data[1]);
            movie.setDurationMinutes(Integer.parseInt(data[2]));
            movie.setDescription(data[3]);
            movie.setRating(Double.parseDouble(data[4]));
            movie.setDirector(data[5]);
            movie.setNowShowing(true);
            movie.setCreatedAt(System.currentTimeMillis());
            movie.setLanguage("English");
            movie.setRated("PG-13");
            movie.setReleaseDate("2024-01-01");
            // Use a placeholder image URL
            movie.setPosterUrl("https://picsum.photos/seed/" + data[0].hashCode() + "/300/450");
            movie.setBackdropUrl("https://picsum.photos/seed/" + data[0].hashCode() + "/800/450");

            firestore.collection("movies").document(movie.getMovieId())
                    .set(movie)
                    .addOnSuccessListener(aVoid -> {
                        android.util.Log.d("MovieRepository", "Seeded: " + movie.getTitle());
                    });
        }
    }

    public void setSelectedMovie(Movie movie) {
        selectedMovie.postValue(movie);
    }

    public void clearSelectedMovie() {
        selectedMovie.postValue(null);
    }
}