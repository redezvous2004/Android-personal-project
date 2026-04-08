package com.example.firebaseandroid.ui.ticket;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.firebaseandroid.databinding.FragmentTicketListBinding;

public class TicketListFragment extends Fragment implements TicketAdapter.OnTicketClickListener {

    private FragmentTicketListBinding binding;
    private TicketListViewModel viewModel;
    private TicketAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTicketListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(TicketListViewModel.class);

        setupRecyclerView();
        observeViewModel();

        viewModel.loadTickets();
    }

    private void setupRecyclerView() {
        adapter = new TicketAdapter(this);
        binding.recyclerViewTickets.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewTickets.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getTickets().observe(getViewLifecycleOwner(), tickets -> {
            if (tickets != null && !tickets.isEmpty()) {
                adapter.submitList(tickets);
                binding.recyclerViewTickets.setVisibility(View.VISIBLE);
                binding.layoutEmpty.setVisibility(View.GONE);
            } else {
                binding.recyclerViewTickets.setVisibility(View.GONE);
                binding.layoutEmpty.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void onTicketClick(com.example.firebaseandroid.data.model.Ticket ticket) {
        Intent intent = new Intent(getContext(), TicketDetailActivity.class);
        intent.putExtra("ticketId", ticket.getTicketId());
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}