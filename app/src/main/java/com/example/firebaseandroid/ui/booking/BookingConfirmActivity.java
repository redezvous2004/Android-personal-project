package com.example.firebaseandroid.ui.booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.firebaseandroid.R;
import com.example.firebaseandroid.data.model.BookingState;
import com.example.firebaseandroid.data.model.Ticket;
import com.example.firebaseandroid.databinding.ActivityBookingConfirmBinding;
import com.example.firebaseandroid.util.FormatUtils;

public class BookingConfirmActivity extends AppCompatActivity {

    private ActivityBookingConfirmBinding binding;
    private BookingViewModel viewModel;
    private BookingState bookingState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingConfirmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(BookingViewModel.class);

        loadBookingState();
        setupViews();
        observeViewModel();
    }

    private void loadBookingState() {
        bookingState = (BookingState) getIntent().getSerializableExtra("bookingState");
        if (bookingState == null) {
            Toast.makeText(this, "Booking error", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupViews() {
        binding.ivBack.setOnClickListener(v -> finish());

        binding.tvMovieTitle.setText(bookingState.getMovie().getTitle());
        binding.tvTheater.setText(bookingState.getTheater().getName());
        binding.tvTheaterAddress.setText(bookingState.getTheater().getAddress());
        binding.tvDateTime.setText(
                FormatUtils.formatDateString(bookingState.getShowtime().getDate())
                + " at " + bookingState.getShowtime().getTime());
        binding.tvSeats.setText("Row " + String.join(", Row ", bookingState.getSelectedSeats()));
        binding.tvTotalPrice.setText(FormatUtils.formatPrice(bookingState.getTotalPrice()));

        binding.btnConfirm.setOnClickListener(v -> {
            binding.btnConfirm.setEnabled(false);
            binding.progressBar.setVisibility(View.VISIBLE);
            confirmBooking();
        });
    }

    private void confirmBooking() {
        String userId = com.example.firebaseandroid.data.repository.AuthRepository
                .getInstance().getFirebaseUser().getUid();

        Ticket ticket = new Ticket(
                userId,
                bookingState.getMovie().getMovieId(),
                bookingState.getTheater().getTheaterId(),
                bookingState.getShowtime().getShowtimeId(),
                bookingState.getMovie().getTitle(),
                bookingState.getTheater().getName(),
                bookingState.getTheater().getAddress(),
                bookingState.getShowtime().getDate(),
                bookingState.getShowtime().getTime(),
                bookingState.getShowtime().getRoomName(),
                bookingState.getSelectedSeats(),
                bookingState.getTotalPrice(),
                bookingState.getMovie().getPosterUrl()
        );

        viewModel.bookTicket(ticket);
    }

    private void observeViewModel() {
        viewModel.getBookedTicket().observe(this, ticket -> {
            if (ticket != null) {
                // Booking success
                Intent intent = new Intent(this, TicketSuccessActivity.class);
                intent.putExtra("bookingState", bookingState);
                intent.putExtra("ticketCode", ticket.getTicketCode());
                intent.putExtra("ticketId", ticket.getTicketId());
                startActivity(intent);
                finish();
            }
        });

        viewModel.getBookingError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnConfirm.setEnabled(true);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
    }
}