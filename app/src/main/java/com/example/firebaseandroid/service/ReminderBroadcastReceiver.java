package com.example.firebaseandroid.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.firebaseandroid.R;
import com.example.firebaseandroid.ui.ticket.TicketDetailActivity;

public class ReminderBroadcastReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "movie_reminder_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!"MOVIE_REMINDER".equals(intent.getAction())) return;

        String ticketId = intent.getStringExtra("ticketId");
        String movieTitle = intent.getStringExtra("movieTitle");
        String theaterName = intent.getStringExtra("theaterName");
        String time = intent.getStringExtra("time");
        String date = intent.getStringExtra("date");
        int minutesBefore = intent.getIntExtra("minutesBefore", 30);

        showReminderNotification(context, movieTitle, theaterName, time, date, ticketId, minutesBefore);
    }

    private void showReminderNotification(Context context, String movieTitle, String theaterName,
                                          String time, String date, String ticketId, int minutesBefore) {
        createChannel(context);

        Intent intent = new Intent(context, TicketDetailActivity.class);
        intent.putExtra("ticketId", ticketId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, ticketId.hashCode(), intent, flags);

        String body = String.format("Your movie \"%s\" starts in %d minutes at %s!",
                movieTitle, minutesBefore, theaterName);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(ticketId.hashCode(), builder.build());
        }
    }

    private void createChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(context.getString(R.string.notification_channel_desc));
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
