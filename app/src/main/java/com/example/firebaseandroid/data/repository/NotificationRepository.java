package com.example.firebaseandroid.data.repository;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.firebaseandroid.data.model.Ticket;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;

public class NotificationRepository {

    private static NotificationRepository instance;
    private final Context context;

    private NotificationRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized NotificationRepository getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationRepository(context);
        }
        return instance;
    }

    /**
     * Subscribe to FCM topic for general notifications
     */
    public void subscribeToGeneralTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("movie_updates")
                .addOnCompleteListener(task -> {
                    android.util.Log.d("NotificationRepo",
                            task.isSuccessful() ? "Subscribed to movie_updates" : "Subscribe failed");
                });
    }

    /**
     * Subscribe to showtime reminder notifications
     * Topic format: showtime_reminder_{movieId}
     */
    public void subscribeToShowtimeReminder(String movieId) {
        FirebaseMessaging.getInstance().subscribeToTopic("showtime_reminder_" + movieId)
                .addOnCompleteListener(task -> {
                    android.util.Log.d("NotificationRepo",
                            task.isSuccessful() ? "Subscribed to reminder_" + movieId : "Subscribe failed");
                });
    }

    /**
     * Schedule local notification reminder for a ticket
     * Uses AlarmManager to trigger notification X minutes before showtime
     */
    public void scheduleShowtimeReminder(Ticket ticket, int minutesBefore) {
        long showtimeTs = ticket.getShowtimeTimestamp();
        long reminderTs = showtimeTs - (minutesBefore * 60 * 1000L);
        long now = System.currentTimeMillis();

        if (reminderTs <= now) {
            android.util.Log.d("NotificationRepo", "Showtime already passed or too soon, skipping reminder");
            return;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, com.example.firebaseandroid.service.ReminderBroadcastReceiver.class);
        intent.setAction("MOVIE_REMINDER");
        intent.putExtra("ticketId", ticket.getTicketId());
        intent.putExtra("movieTitle", ticket.getMovieTitle());
        intent.putExtra("theaterName", ticket.getTheaterName());
        intent.putExtra("time", ticket.getTime());
        intent.putExtra("date", ticket.getDate());
        intent.putExtra("minutesBefore", minutesBefore);

        int requestCode = ticket.getTicketId().hashCode();
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent, flags);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminderTs,
                        pendingIntent
                );
            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        reminderTs,
                        pendingIntent
                );
            }
            android.util.Log.d("NotificationRepo",
                    "Reminder scheduled for " + minutesBefore + " min before at " + reminderTs);
        } catch (SecurityException e) {
            android.util.Log.e("NotificationRepo", "Failed to schedule alarm: permission denied");
        }
    }

    /**
     * Cancel scheduled reminder for a ticket
     */
    public void cancelShowtimeReminder(Ticket ticket) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, com.example.firebaseandroid.service.ReminderBroadcastReceiver.class);
        intent.setAction("MOVIE_REMINDER");

        int requestCode = ticket.getTicketId().hashCode();
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent, flags);

        alarmManager.cancel(pendingIntent);
        android.util.Log.d("NotificationRepo", "Reminder cancelled for ticket: " + ticket.getTicketId());
    }

    /**
     * Schedule reminders for all upcoming tickets
     */
    public void scheduleRemindersForAllTickets(List<Ticket> tickets, int minutesBefore) {
        long now = System.currentTimeMillis();
        for (Ticket ticket : tickets) {
            if ("upcoming".equals(ticket.getStatus()) && ticket.getShowtimeTimestamp() > now) {
                scheduleShowtimeReminder(ticket, minutesBefore);
            }
        }
    }
}