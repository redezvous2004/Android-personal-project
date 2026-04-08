package com.example.firebaseandroid.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.firebaseandroid.data.model.Showtime;
import com.example.firebaseandroid.data.model.Theater;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TheaterRepository {

    private static TheaterRepository instance;
    private final FirebaseFirestore firestore;
    private final MutableLiveData<List<Theater>> theaters = new MutableLiveData<>();
    private final MutableLiveData<List<Showtime>> showtimes = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private TheaterRepository() {
        firestore = FirebaseFirestore.getInstance();
    }

    public static synchronized TheaterRepository getInstance() {
        if (instance == null) {
            instance = new TheaterRepository();
        }
        return instance;
    }

    public LiveData<List<Theater>> getTheaters() {
        return theaters;
    }

    public LiveData<List<Showtime>> getShowtimes() {
        return showtimes;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    /**
     * Load all theaters
     */
    public void loadTheaters() {
        isLoading.postValue(true);
        firestore.collection("theaters")
                .orderBy("name")
                .get()
                .addOnCompleteListener(task -> {
                    isLoading.postValue(false);
                    if (task.isSuccessful()) {
                        List<Theater> list = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Theater theater = doc.toObject(Theater.class);
                            theater.setTheaterId(doc.getId());
                            list.add(theater);
                        }
                        theaters.postValue(list);
                    }
                });
    }

    /**
     * Load showtimes for a specific movie
     */
    public void loadShowtimesForMovie(String movieId) {
        isLoading.postValue(true);
        firestore.collection("showtimes")
                .whereEqualTo("movieId", movieId)
                .orderBy("date")
                .get()
                .addOnCompleteListener(task -> {
                    isLoading.postValue(false);
                    if (task.isSuccessful()) {
                        List<Showtime> list = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Showtime showtime = doc.toObject(Showtime.class);
                            showtime.setShowtimeId(doc.getId());
                            list.add(showtime);
                        }
                        showtimes.postValue(list);
                    }
                });
    }

    /**
     * Load showtimes for a specific movie and date
     */
    public void loadShowtimesForMovieAndDate(String movieId, String date) {
        isLoading.postValue(true);
        firestore.collection("showtimes")
                .whereEqualTo("movieId", movieId)
                .whereEqualTo("date", date)
                .orderBy("time")
                .get()
                .addOnCompleteListener(task -> {
                    isLoading.postValue(false);
                    if (task.isSuccessful()) {
                        List<Showtime> list = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Showtime showtime = doc.toObject(Showtime.class);
                            showtime.setShowtimeId(doc.getId());
                            list.add(showtime);
                        }
                        showtimes.postValue(list);
                    }
                });
    }

    /**
     * Seed sample theaters
     */
    public void seedSampleTheaters() {
        String[][] theatersData = {
            {"Galaxy Cinema Nguyen Trai", "369 Nguyen Trai, Ward 7, District 5, Ho Chi Minh City", "Ho Chi Minh City"},
            {"CGV Vincom Center Dong Khoi", "72 Nguyen Hue, District 1, Ho Chi Minh City", "Ho Chi Minh City"},
            {"Lotte Cinema Landmark 81", "B1, Landmark 81, 208 Nguyen Huu Canh, Binh Thanh District, Ho Chi Minh City", "Ho Chi Minh City"},
            {"CGV Aeon Tan Binh", "L2, Aeon Tan Binh, 30 Baa Bui, Ward 15, Tan Binh District, Ho Chi Minh City", "Ho Chi Minh City"},
        };

        for (String[] data : theatersData) {
            Theater theater = new Theater();
            theater.setTheaterId(firestore.collection("theaters").document().getId());
            theater.setName(data[0]);
            theater.setAddress(data[1]);
            theater.setCity(data[2]);
            theater.setTotalSeats(100);
            theater.setPhoneNumber("1900 6017");

            firestore.collection("theaters").document(theater.getTheaterId())
                    .set(theater)
                    .addOnSuccessListener(aVoid -> {
                        seedShowtimesForTheater(theater.getTheaterId());
                    });
        }
    }

    private void seedShowtimesForTheater(String theaterId) {
        // Seed showtimes for the next 7 days
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        java.util.Calendar cal = java.util.Calendar.getInstance();

        String[] times = {"09:00", "11:30", "14:00", "16:30", "19:00", "21:30"};
        long[] prices = {60000, 70000, 80000};

        for (int day = 0; day < 7; day++) {
            String date = sdf.format(cal.getTime());
            for (String time : times) {
                for (long price : prices) {
                    Showtime showtime = new Showtime();
                    showtime.setShowtimeId(firestore.collection("showtimes").document().getId());
                    showtime.setTheaterId(theaterId);
                    showtime.setMovieId("sample_movie_" + (int)(Math.random() * 5 + 1));
                    showtime.setDate(date);
                    showtime.setTime(time);
                    showtime.setTotalSeats(100);
                    showtime.setAvailableSeats((int)(Math.random() * 60 + 40));
                    showtime.setPrice(price);
                    showtime.setRoomName("Room " + ((int)(Math.random() * 5 + 1)));

                    firestore.collection("showtimes").document(showtime.getShowtimeId())
                            .set(showtime);
                }
            }
            cal.add(java.util.Calendar.DAY_OF_YEAR, 1);
        }
    }

    /**
     * Get theater by ID from cached list
     */
    public Theater getTheaterById(String theaterId) {
        List<Theater> list = theaters.getValue();
        if (list != null) {
            for (Theater t : list) {
                if (t.getTheaterId().equals(theaterId)) {
                    return t;
                }
            }
        }
        return null;
    }
}