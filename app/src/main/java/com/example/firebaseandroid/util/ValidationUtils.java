package com.example.firebaseandroid.util;

import android.content.Context;
import android.util.Patterns;

public class ValidationUtils {

    public static String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Email is required";
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            return "Please enter a valid email address";
        }
        return null;
    }

    public static String validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return "Password is required";
        }
        if (password.length() < 6) {
            return "Password must be at least 6 characters";
        }
        return null;
    }

    public static String validateConfirmPassword(String password, String confirmPassword) {
        if (confirmPassword == null || confirmPassword.isEmpty()) {
            return "Please confirm your password";
        }
        if (!password.equals(confirmPassword)) {
            return "Passwords do not match";
        }
        return null;
    }

    public static String validateFullName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Full name is required";
        }
        if (name.trim().length() < 2) {
            return "Full name must be at least 2 characters";
        }
        return null;
    }

    public static String validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return "Phone number is required";
        }
        // Vietnamese phone number format
        String cleaned = phone.replaceAll("[\\s\\-()]", "");
        if (!cleaned.matches("^0[3-9]\\d{8,9}$")) {
            return "Please enter a valid Vietnamese phone number";
        }
        return null;
    }
}