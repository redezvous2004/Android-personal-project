package com.example.firebaseandroid.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.firebaseandroid.data.model.Ticket;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketRepository {

    private static TicketRepository instance;
    private final FirebaseFirestore firestore;
    private final FirebaseAuth firebaseAuth;

    private final MutableLiveData<List<Ticket>> userTickets = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Ticket> bookedTicket = new MutableLiveData<>();

    private TicketRepository() {
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public static synchronized TicketRepository getInstance() {
        if (instance == null) {
            instance = new TicketRepository();
        }
        return instance;
    }

    public LiveData<List<Ticket>> getUserTickets() {
        return userTickets;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Ticket> getBookedTicket() {
        return bookedTicket;
    }

    /**
     * Load tickets for current logged-in user
     */
    public void loadUserTickets() {
        String userId = firebaseAuth.getCurrentUser() != null ? firebaseAuth.getCurrentUser().getUid() : null;
        if (userId == null) {
            userTickets.postValue(new ArrayList<>());
            return;
        }

        isLoading.postValue(true);
        error.postValue(null);

        firestore.collection("tickets")
                .whereEqualTo("userId", userId)
                .orderBy("bookedAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    isLoading.postValue(false);
                    if (task.isSuccessful()) {
                        List<Ticket> list = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Ticket ticket = doc.toObject(Ticket.class);
                            ticket.setTicketId(doc.getId());
                            list.add(ticket);
                        }
                        userTickets.postValue(list);
                    } else {
                        error.postValue("Failed to load tickets");
                    }
                });
    }

    /**
     * Book a ticket - save to Firestore
     */
    public void bookTicket(Ticket ticket, OnCompleteListener<DocumentReference> callback) {
        isLoading.postValue(true);
        error.postValue(null);

        // Save ticket to Firestore
        Map<String, Object> ticketData = new HashMap<>();
        ticketData.put("ticketId", ticket.getTicketId());
        ticketData.put("userId", ticket.getUserId());
        ticketData.put("movieId", ticket.getMovieId());
        ticketData.put("theaterId", ticket.getTheaterId());
        ticketData.put("showtimeId", ticket.getShowtimeId());
        ticketData.put("movieTitle", ticket.getMovieTitle());
        ticketData.put("theaterName", ticket.getTheaterName());
        ticketData.put("theaterAddress", ticket.getTheaterAddress());
        ticketData.put("date", ticket.getDate());
        ticketData.put("time", ticket.getTime());
        ticketData.put("roomName", ticket.getRoomName());
        ticketData.put("seatNumbers", ticket.getSeatNumbers());
        ticketData.put("totalPrice", ticket.getTotalPrice());
        ticketData.put("ticketCode", ticket.getTicketCode());
        ticketData.put("bookedAt", ticket.getBookedAt());
        ticketData.put("status", ticket.getStatus());
        ticketData.put("posterUrl", ticket.getPosterUrl());

        firestore.collection("tickets")
                .add(ticketData)
                .addOnCompleteListener(task -> {
                    isLoading.postValue(false);
                    if (task.isSuccessful()) {
                        String ticketId = task.getResult().getId();
                        ticket.setTicketId(ticketId);
                        bookedTicket.postValue(ticket);
                    } else {
                        error.postValue("Failed to book ticket. Please try again.");
                    }
                    if (callback != null) callback.onComplete(task);
                });
    }

    /**
     * Cancel a ticket
     */
    public void cancelTicket(String ticketId, OnCompleteListener<Void> callback) {
        isLoading.postValue(true);
        firestore.collection("tickets")
                .document(ticketId)
                .update("status", "cancelled")
                .addOnCompleteListener(task -> {
                    isLoading.postValue(false);
                    if (callback != null) callback.onComplete(task);
                    // Reload tickets
                    if (task.isSuccessful()) {
                        loadUserTickets();
                    }
                });
    }

    /**
     * Get a single ticket by ID
     */
    public void getTicketById(String ticketId, OnCompleteListener<DocumentSnapshot> callback) {
        firestore.collection("tickets")
                .document(ticketId)
                .get()
                .addOnCompleteListener(callback);
    }

    /**
     * Get upcoming tickets only
     */
    public List<Ticket> getUpcomingTickets() {
        List<Ticket> all = userTickets.getValue();
        if (all == null) return new ArrayList<>();

        List<Ticket> upcoming = new ArrayList<>();
        long now = System.currentTimeMillis();
        for (Ticket t : all) {
            if ("upcoming".equals(t.getStatus()) && t.getShowtimeTimestamp() > now) {
                upcoming.add(t);
            }
        }
        return upcoming;
    }

    public void clearBookedTicket() {
        bookedTicket.postValue(null);
    }
}