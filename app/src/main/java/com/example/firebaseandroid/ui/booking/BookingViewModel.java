package com.example.firebaseandroid.ui.booking;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.firebaseandroid.data.model.BookingState;
import com.example.firebaseandroid.data.model.Showtime;
import com.example.firebaseandroid.data.model.Theater;
import com.example.firebaseandroid.data.model.Ticket;
import com.example.firebaseandroid.data.repository.TheaterRepository;
import com.example.firebaseandroid.data.repository.TicketRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class BookingViewModel extends ViewModel {

    private final TheaterRepository theaterRepository;
    private final TicketRepository ticketRepository;
    private final MutableLiveData<BookingState> bookingState = new MutableLiveData<>();

    public BookingViewModel() {
        theaterRepository = TheaterRepository.getInstance();
        ticketRepository = TicketRepository.getInstance();
    }

    public LiveData<List<Theater>> getTheaters() {
        return theaterRepository.getTheaters();
    }

    public LiveData<List<Showtime>> getShowtimes() {
        return theaterRepository.getShowtimes();
    }

    public LiveData<Boolean> getIsLoading() {
        return theaterRepository.getIsLoading();
    }

    public LiveData<Ticket> getBookedTicket() {
        return ticketRepository.getBookedTicket();
    }

    public LiveData<BookingState> getBookingState() {
        return bookingState;
    }

    public LiveData<Boolean> getBookingLoading() {
        return ticketRepository.getIsLoading();
    }

    public LiveData<String> getBookingError() {
        return ticketRepository.getError();
    }

    public void setBookingState(BookingState state) {
        bookingState.setValue(state);
    }

    public void loadTheaters() {
        theaterRepository.loadTheaters();
    }

    public void loadShowtimes(String movieId) {
        theaterRepository.loadShowtimesForMovie(movieId);
    }

    public void bookTicket(Ticket ticket) {
        ticketRepository.bookTicket(ticket, null);
    }
}
