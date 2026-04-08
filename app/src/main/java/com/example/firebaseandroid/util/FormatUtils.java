package com.example.firebaseandroid.util;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatUtils {

    private static final Locale VIETNAM_LOCALE = new Locale("vi", "VN");

    /**
     * Format price in VND with Vietnamese locale
     */
    public static String formatPrice(long price) {
        NumberFormat nf = NumberFormat.getNumberInstance(VIETNAM_LOCALE);
        return nf.format(price) + " VNĐ";
    }

    /**
     * Format timestamp to readable date string
     */
    public static String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", VIETNAM_LOCALE);
        return sdf.format(new Date(timestamp));
    }

    /**
     * Format timestamp to readable date + time string
     */
    public static String formatDateTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", VIETNAM_LOCALE);
        return sdf.format(new Date(timestamp));
    }

    /**
     * Format date from "yyyy-MM-dd" to "dd/MM/yyyy"
     */
    public static String formatDateString(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return "";
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy", VIETNAM_LOCALE);
            Date date = input.parse(dateStr);
            return date != null ? output.format(date) : dateStr;
        } catch (Exception e) {
            return dateStr;
        }
    }

    /**
     * Format time from "HH:mm" to "HH:mm"
     */
    public static String formatTime(String timeStr) {
        return timeStr != null ? timeStr : "";
    }

    /**
     * Format movie duration
     */
    public static String formatDuration(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        if (hours > 0) {
            return hours + "h " + mins + "m";
        }
        return mins + " min";
    }

    /**
     * Format rating score
     */
    public static String formatRating(double rating) {
        return String.format(Locale.getDefault(), "%.1f", rating);
    }
}