package com.example.firebaseandroid.data.model;

public class Showtime {
    private String showtimeId;
    private String movieId;
    private String theaterId;
    private String date; // yyyy-MM-dd
    private String time; // HH:mm
    private int availableSeats;
    private int totalSeats;
    private long price; // VND
    private String roomName;

    public Showtime() {}

    public Showtime(String showtimeId, String movieId, String theaterId, String date, String time, int totalSeats, long price) {
        this.showtimeId = showtimeId;
        this.movieId = movieId;
        this.theaterId = theaterId;
        this.date = date;
        this.time = time;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
        this.price = price;
    }

    public String getShowtimeId() { return showtimeId; }
    public void setShowtimeId(String showtimeId) { this.showtimeId = showtimeId; }

    public String getMovieId() { return movieId; }
    public void setMovieId(String movieId) { this.movieId = movieId; }

    public String getTheaterId() { return theaterId; }
    public void setTheaterId(String theaterId) { this.theaterId = theaterId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    public long getPrice() { return price; }
    public void setPrice(long price) { this.price = price; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    // Get timestamp for scheduling notification (milliseconds)
    public long getShowtimeTimestamp() {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
            java.util.Date dateObj = sdf.parse(this.date + " " + this.time);
            return dateObj != null ? dateObj.getTime() : 0;
        } catch (Exception e) {
            return 0;
        }
    }
}