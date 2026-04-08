package com.example.firebaseandroid.ui.booking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firebaseandroid.data.model.BookingState;
import com.example.firebaseandroid.data.model.Ticket;
import com.example.firebaseandroid.data.repository.NotificationRepository;
import com.example.firebaseandroid.databinding.ActivityTicketSuccessBinding;
import com.example.firebaseandroid.ui.main.MainActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.HashMap;
import java.util.Map;

public class TicketSuccessActivity extends AppCompatActivity {

    private ActivityTicketSuccessBinding binding;
    private BookingState bookingState;
    private Ticket ticket;
    private NotificationRepository notificationRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTicketSuccessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        notificationRepo = NotificationRepository.getInstance(this);
        loadData();
        setupViews();
        generateQRCode();
        scheduleReminder();
    }

    private void loadData() {
        bookingState = (BookingState) getIntent().getSerializableExtra("bookingState");
        String ticketCode = getIntent().getStringExtra("ticketCode");
        String ticketId = getIntent().getStringExtra("ticketId");

        // Create ticket from booking state
        String userId = com.example.firebaseandroid.data.repository.AuthRepository
                .getInstance().getFirebaseUser().getUid();
        ticket = new Ticket(
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
        if (ticketCode != null) ticket.setTicketCode(ticketCode);
        if (ticketId != null) ticket.setTicketId(ticketId);
    }

    private void setupViews() {
        binding.tvMovieTitle.setText(bookingState.getMovie().getTitle());
        binding.tvTheater.setText(bookingState.getTheater().getName());
        binding.tvTicketCode.setText(ticket.getTicketCode());
        binding.tvSeats.setText("Seats: " + String.join(", ", bookingState.getSelectedSeats()));

        binding.btnViewTickets.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("navigate_to", "tickets");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        binding.btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void generateQRCode() {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 2);

            int width = 512;
            int height = 512;

            BitMatrix bitMatrix = writer.encode(ticket.getTicketCode(), BarcodeFormat.QR_CODE, width, height, hints);

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            binding.ivQrCode.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scheduleReminder() {
        // Schedule reminder 30 minutes before showtime
        notificationRepo.scheduleShowtimeReminder(ticket, 30);
    }

    @Override
    public void onBackPressed() {
        // Go to main instead of back to booking
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}