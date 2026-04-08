package com.example.firebaseandroid.ui.ticket;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.firebaseandroid.data.model.Ticket;
import com.example.firebaseandroid.data.repository.TicketRepository;

import java.util.List;

public class TicketListViewModel extends ViewModel {

    private final TicketRepository repository;

    public TicketListViewModel() {
        repository = TicketRepository.getInstance();
    }

    public LiveData<List<Ticket>> getTickets() {
        return repository.getUserTickets();
    }

    public LiveData<Boolean> getIsLoading() {
        return repository.getIsLoading();
    }

    public LiveData<String> getError() {
        return repository.getError();
    }

    public void loadTickets() {
        repository.loadUserTickets();
    }
}