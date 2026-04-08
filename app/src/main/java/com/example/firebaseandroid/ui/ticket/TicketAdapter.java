package com.example.firebaseandroid.ui.ticket;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firebaseandroid.R;
import com.example.firebaseandroid.data.model.Ticket;
import com.example.firebaseandroid.databinding.ItemTicketBinding;
import com.example.firebaseandroid.util.FormatUtils;

import java.util.List;

public class TicketAdapter extends ListAdapter<Ticket, TicketAdapter.TicketViewHolder> {

    private final OnTicketClickListener listener;

    public interface OnTicketClickListener {
        void onTicketClick(Ticket ticket);
    }

    public TicketAdapter(OnTicketClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Ticket> DIFF_CALLBACK = new DiffUtil.ItemCallback<Ticket>() {
        @Override
        public boolean areItemsTheSame(@NonNull Ticket oldItem, @NonNull Ticket newItem) {
            return oldItem.getTicketId() != null && oldItem.getTicketId().equals(newItem.getTicketId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Ticket oldItem, @NonNull Ticket newItem) {
            return oldItem.getTicketCode().equals(newItem.getTicketCode()) &&
                   oldItem.getStatus().equals(newItem.getStatus());
        }
    };

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTicketBinding binding = ItemTicketBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new TicketViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class TicketViewHolder extends RecyclerView.ViewHolder {
        private final ItemTicketBinding binding;

        TicketViewHolder(ItemTicketBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Ticket ticket) {
            binding.tvMovieTitle.setText(ticket.getMovieTitle());
            binding.tvTheater.setText(ticket.getTheaterName());
            binding.tvDateTime.setText(FormatUtils.formatDateString(ticket.getDate()) + " • " + ticket.getTime());
            binding.tvSeats.setText("Seats: " + String.join(", ", ticket.getSeatNumbers()));
            binding.tvTicketCode.setText(ticket.getTicketCode());
            binding.tvPrice.setText(FormatUtils.formatPrice(ticket.getTotalPrice()));

            // Status badge
            binding.tvStatus.setText(ticket.getStatus().toUpperCase());
            int statusColor;
            switch (ticket.getStatus()) {
                case "upcoming":
                    statusColor = binding.getRoot().getContext().getColor(R.color.success);
                    break;
                case "cancelled":
                    statusColor = binding.getRoot().getContext().getColor(R.color.error);
                    break;
                default:
                    statusColor = binding.getRoot().getContext().getColor(R.color.text_secondary);
            }
            binding.tvStatus.setTextColor(statusColor);

            // Poster
            if (ticket.getPosterUrl() != null && !ticket.getPosterUrl().isEmpty()) {
                Glide.with(binding.getRoot().getContext())
                        .load(ticket.getPosterUrl())
                        .placeholder(R.drawable.bg_movie_placeholder)
                        .error(R.drawable.bg_movie_placeholder)
                        .centerCrop()
                        .into(binding.ivPoster);
            } else {
                binding.ivPoster.setImageResource(R.drawable.bg_movie_placeholder);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onTicketClick(ticket);
            });
        }
    }
}