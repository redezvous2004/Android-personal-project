package com.example.firebaseandroid.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds the booking flow state - passed between booking activities.
 */
public class BookingState implements Serializable {
    private Movie movie;
    private Theater theater;
    private Showtime showtime;
    private List<String> selectedSeats = new ArrayList<>();

    public BookingState() {}

    public Movie getMovie() { return movie; }
    public void setMovie(Movie movie) { this.movie = movie; }

    public Theater getTheater() { return theater; }
    public void setTheater(Theater theater) { this.theater = theater; }

    public Showtime getShowtime() { return showtime; }
    public void setShowtime(Showtime showtime) { this.showtime = showtime; }

    public List<String> getSelectedSeats() { return selectedSeats; }
    public void setSelectedSeats(List<String> selectedSeats) { this.selectedSeats = selectedSeats; }

    public long getTotalPrice() {
        if (showtime == null) return 0;
        return showtime.getPrice() * selectedSeats.size();
    }

    public void addSeat(String seat) {
        if (!selectedSeats.contains(seat)) {
            selectedSeats.add(seat);
        }
    }

    public void removeSeat(String seat) {
        selectedSeats.remove(seat);
    }

    public void clearSeats() {
        selectedSeats.clear();
    }

    public boolean hasValidSelection() {
        return movie != null && theater != null && showtime != null && !selectedSeats.isEmpty();
    }
}