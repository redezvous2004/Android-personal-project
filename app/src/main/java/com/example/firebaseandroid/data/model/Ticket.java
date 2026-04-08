package com.example.firebaseandroid.data.model;

import java.util.List;
import java.util.UUID;

public class Ticket {
    private String ticketId;
    private String userId;
    private String movieId;
    private String theaterId;
    private String showtimeId;
    private String movieTitle;
    private String theaterName;
    private String theaterAddress;
    private String date;
    private String time;
    private String roomName;
    private List<String> seatNumbers;
    private long totalPrice;
    private String ticketCode; // QR code content
    private long bookedAt;
    private String status; // upcoming, watched, cancelled
    private String posterUrl;

    public Ticket() {}

    public Ticket(String userId, String movieId, String theaterId, String showtimeId,
                  String movieTitle, String theaterName, String theaterAddress,
                  String date, String time, String roomName, List<String> seatNumbers, long totalPrice, String posterUrl) {
        this.ticketId = UUID.randomUUID().toString();
        this.userId = userId;
        this.movieId = movieId;
        this.theaterId = theaterId;
        this.showtimeId = showtimeId;
        this.movieTitle = movieTitle;
        this.theaterName = theaterName;
        this.theaterAddress = theaterAddress;
        this.date = date;
        this.time = time;
        this.roomName = roomName;
        this.seatNumbers = seatNumbers;
        this.totalPrice = totalPrice;
        this.ticketCode = generateTicketCode();
        this.bookedAt = System.currentTimeMillis();
        this.status = "upcoming";
        this.posterUrl = posterUrl;
    }

    private String generateTicketCode() {
        return "MT-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 900 + 100);
    }

    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getMovieId() { return movieId; }
    public void setMovieId(String movieId) { this.movieId = movieId; }

    public String getTheaterId() { return theaterId; }
    public void setTheaterId(String theaterId) { this.theaterId = theaterId; }

    public String getShowtimeId() { return showtimeId; }
    public void setShowtimeId(String showtimeId) { this.showtimeId = showtimeId; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getTheaterName() { return theaterName; }
    public void setTheaterName(String theaterName) { this.theaterName = theaterName; }

    public String getTheaterAddress() { return theaterAddress; }
    public void setTheaterAddress(String theaterAddress) { this.theaterAddress = theaterAddress; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public List<String> getSeatNumbers() { return seatNumbers; }
    public void setSeatNumbers(List<String> seatNumbers) { this.seatNumbers = seatNumbers; }

    public long getTotalPrice() { return totalPrice; }
    public void setTotalPrice(long totalPrice) { this.totalPrice = totalPrice; }

    public String getTicketCode() { return ticketCode; }
    public void setTicketCode(String ticketCode) { this.ticketCode = ticketCode; }

    public long getBookedAt() { return bookedAt; }
    public void setBookedAt(long bookedAt) { this.bookedAt = bookedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    // Get showtime timestamp for reminder scheduling
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