package com.example.firebaseandroid.ui.ticket;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.firebaseandroid.R;
import com.example.firebaseandroid.data.model.Ticket;
import com.example.firebaseandroid.data.repository.NotificationRepository;
import com.example.firebaseandroid.data.repository.TicketRepository;
import com.example.firebaseandroid.databinding.ActivityTicketDetailBinding;
import com.example.firebaseandroid.util.FormatUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketDetailActivity extends AppCompatActivity {

    private ActivityTicketDetailBinding binding;
    private TicketRepository ticketRepository;
    private NotificationRepository notificationRepo;
    private Ticket ticket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTicketDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ticketRepository = TicketRepository.getInstance();
        notificationRepo = NotificationRepository.getInstance(this);

        String ticketId = getIntent().getStringExtra("ticketId");
        loadTicket(ticketId);

        binding.ivBack.setOnClickListener(v -> finish());
    }

    private void loadTicket(String ticketId) {
        ticketRepository.getTicketById(ticketId, task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                ticket = task.getResult().toObject(Ticket.class);
                if (ticket != null) {
                    runOnUiThread(this::displayTicket);
                }
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Ticket not found", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void displayTicket() {
        binding.tvMovieTitle.setText(ticket.getMovieTitle());
        binding.tvTheater.setText(ticket.getTheaterName());
        binding.tvAddress.setText(ticket.getTheaterAddress());
        binding.tvDateTime.setText(
                FormatUtils.formatDateString(ticket.getDate()) + " at " + ticket.getTime());
        binding.tvRoom.setText(ticket.getRoomName());
        binding.tvSeats.setText(String.join(", ", ticket.getSeatNumbers()));
        binding.tvPrice.setText(FormatUtils.formatPrice(ticket.getTotalPrice()));
        binding.tvTicketCode.setText(ticket.getTicketCode());
        binding.tvBookingDate.setText("Booked: " + FormatUtils.formatDateTime(ticket.getBookedAt()));
        binding.tvStatus.setText(ticket.getStatus().toUpperCase());

        // Status color
        int statusColor;
        switch (ticket.getStatus()) {
            case "upcoming": statusColor = getColor(R.color.success); break;
            case "cancelled": statusColor = getColor(R.color.error); break;
            default: statusColor = getColor(R.color.text_secondary);
        }
        binding.tvStatus.setTextColor(statusColor);

        // Show/hide cancel button
        boolean canCancel = "upcoming".equals(ticket.getStatus()) &&
                ticket.getShowtimeTimestamp() > System.currentTimeMillis();
        binding.btnCancel.setVisibility(canCancel ? View.VISIBLE : View.GONE);

        // Poster
        if (ticket.getPosterUrl() != null && !ticket.getPosterUrl().isEmpty()) {
            Glide.with(this)
                    .load(ticket.getPosterUrl())
                    .placeholder(R.drawable.bg_movie_placeholder)
                    .into(binding.ivPoster);
        }

        // QR Code
        generateQRCode(ticket.getTicketCode());

        // Cancel
        binding.btnCancel.setOnClickListener(v -> showCancelDialog());
    }

    private void generateQRCode(String code) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 2);
            BitMatrix bitMatrix = writer.encode(code, BarcodeFormat.QR_CODE, 512, 512, hints);
            Bitmap bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565);
            for (int x = 0; x < 512; x++) {
                for (int y = 0; y < 512; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            binding.ivQrCode.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCancelDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.cancel_booking))
                .setMessage(getString(R.string.cancel_booking_confirm))
                .setPositiveButton(getString(R.string.yes_cancel), (d, w) -> cancelTicket())
                .setNegativeButton(getString(R.string.cancel_action), null)
                .show();
    }

    private void cancelTicket() {
        if (ticket == null) return;
        ticketRepository.cancelTicket(ticket.getTicketId(), task -> {
            runOnUiThread(() -> {
                if (task.isSuccessful()) {
                    notificationRepo.cancelShowtimeReminder(ticket);
                    Toast.makeText(this, "Ticket cancelled", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to cancel", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
