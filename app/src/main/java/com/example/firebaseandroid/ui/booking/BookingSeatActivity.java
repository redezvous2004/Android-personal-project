package com.example.firebaseandroid.ui.booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.firebaseandroid.R;
import com.example.firebaseandroid.data.model.BookingState;
import com.example.firebaseandroid.databinding.ActivityBookingSeatBinding;
import com.example.firebaseandroid.util.FormatUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BookingSeatActivity extends AppCompatActivity {

    private ActivityBookingSeatBinding binding;
    private BookingViewModel viewModel;
    private BookingState bookingState;

    private static final int ROWS = 8;
    private static final int COLS = 10;
    private static final char ROW_LABEL = 'A';
    private static final int MAX_SEATS = 8;

    private Set<String> occupiedSeats = new HashSet<>();
    private List<String> selectedSeats = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingSeatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new BookingViewModel();

        loadBookingState();
        setupViews();
        generateSeats();
    }

    private void loadBookingState() {
        bookingState = (BookingState) getIntent().getSerializableExtra("bookingState");
        if (bookingState == null) {
            Toast.makeText(this, "Booking error. Please try again.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupViews() {
        binding.ivBack.setOnClickListener(v -> finish());

        binding.tvMovieTitle.setText(bookingState.getMovie().getTitle());
        binding.tvTheater.setText(bookingState.getTheater().getName());
        binding.tvShowtime.setText(bookingState.getShowtime().getTime());

        // Randomly mark some seats as occupied (simulating real booking)
        generateOccupiedSeats();

        binding.btnProceed.setOnClickListener(v -> {
            if (selectedSeats.isEmpty()) {
                Toast.makeText(this, "Please select at least one seat", Toast.LENGTH_SHORT).show();
                return;
            }

            bookingState.setSelectedSeats(new ArrayList<>(selectedSeats));

            Intent intent = new Intent(this, BookingConfirmActivity.class);
            intent.putExtra("bookingState", bookingState);
            startActivity(intent);
        });

        updateTotalPrice();
    }

    private void generateOccupiedSeats() {
        // Simulate some already-booked seats
        String[] occupiedPatterns = {"A2", "A3", "A4", "B1", "B5", "C3", "C4",
                                      "D6", "D7", "E1", "E2", "F8", "G3", "H5"};
        for (String s : occupiedPatterns) {
            occupiedSeats.add(s);
        }
    }

    private void generateSeats() {
        binding.seatContainer.removeAllViews();

        // Screen label
        TextView screenLabel = new TextView(this);
        screenLabel.setText("SCREEN");
        screenLabel.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        screenLabel.setTextSize(12);
        screenLabel.setGravity(android.view.Gravity.CENTER);
        screenLabel.setPadding(0, 16, 0, 16);

        GridLayout.LayoutParams screenParams = new GridLayout.LayoutParams();
        screenParams.width = GridLayout.LayoutParams.MATCH_PARENT;
        screenParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
        screenParams.columnSpec = GridLayout.spec(0, COLS);
        binding.seatContainer.addView(screenLabel, screenParams);

        // Seat grid
        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(COLS);
        grid.setRowCount(ROWS);
        grid.setPadding(8, 8, 8, 8);

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                char rowChar = (char) (ROW_LABEL + r);
                String seatId = String.valueOf(rowChar) + (c + 1);
                boolean isOccupied = occupiedSeats.contains(seatId);

                TextView seat = createSeatView(seatId, isOccupied);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                params.columnSpec = GridLayout.spec(c, 1f);
                params.setMargins(4, 4, 4, 4);
                grid.addView(seat, params);
            }
        }

        GridLayout.LayoutParams gridParams = new GridLayout.LayoutParams();
        gridParams.width = GridLayout.LayoutParams.MATCH_PARENT;
        gridParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
        binding.seatContainer.addView(grid, gridParams);

        // Legend
        addLegend();
    }

    private TextView createSeatView(String seatId, boolean isOccupied) {
        TextView seat = new TextView(this);
        seat.setText(seatId);
        seat.setTextSize(11);
        seat.setGravity(android.view.Gravity.CENTER);
        seat.setPadding(8, 12, 8, 12);

        if (isOccupied) {
            seat.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_seat_occupied));
            seat.setTextColor(ContextCompat.getColor(this, R.color.text_hint));
            seat.setEnabled(false);
        } else {
            seat.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_seat_available));
            seat.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            seat.setOnClickListener(v -> onSeatClick(seat, seatId));
        }

        return seat;
    }

    private void onSeatClick(TextView seatView, String seatId) {
        if (selectedSeats.contains(seatId)) {
            // Deselect
            selectedSeats.remove(seatId);
            seatView.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_seat_available));
            seatView.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        } else {
            if (selectedSeats.size() >= MAX_SEATS) {
                Toast.makeText(this, "Maximum " + MAX_SEATS + " seats per booking", Toast.LENGTH_SHORT).show();
                return;
            }
            // Select
            selectedSeats.add(seatId);
            seatView.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_seat_selected));
            seatView.setTextColor(ContextCompat.getColor(this, R.color.text_on_primary));
        }
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        long total = bookingState.getShowtime().getPrice() * selectedSeats.size();
        binding.tvSeatsSelected.setText(selectedSeats.size() + " seat(s) selected");
        binding.tvTotalPrice.setText(FormatUtils.formatPrice(total));
        binding.btnProceed.setEnabled(!selectedSeats.isEmpty());
    }

    private void addLegend() {
        // Already in XML
    }
}
