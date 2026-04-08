package com.example.firebaseandroid.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AuthRepository {

    private static AuthRepository instance;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;

    private final MutableLiveData<FirebaseUser> currentUser = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> authError = new MutableLiveData<>();

    private AuthRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Listen to auth state changes
        firebaseAuth.addAuthStateListener(firebaseAuth -> {
            currentUser.postValue(firebaseAuth.getCurrentUser());
        });
    }

    public static synchronized AuthRepository getInstance() {
        if (instance == null) {
            instance = new AuthRepository();
        }
        return instance;
    }

    public LiveData<FirebaseUser> getCurrentUser() {
        return currentUser;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getAuthError() {
        return authError;
    }

    public void clearError() {
        authError.postValue(null);
    }

    /**
     * Register new user with email and password
     */
    public void register(String email, String password, String fullName, String phone,
                         OnCompleteListener<AuthResult> callback) {
        isLoading.postValue(true);
        clearError();

        firebaseAuth.createUserWithEmailAndPassword(email.trim(), password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            createUserDocument(user.getUid(), email.trim(), fullName.trim(), phone.trim(), task2 -> {
                                isLoading.postValue(false);
                                if (!task2.isSuccessful()) {
                                    authError.postValue("Failed to save user data.");
                                }
                                if (callback != null) callback.onComplete(task);
                            });
                        } else {
                            isLoading.postValue(false);
                            if (callback != null) callback.onComplete(task);
                        }
                    } else {
                        isLoading.postValue(false);
                        String error = getAuthErrorMessage(task.getException());
                        authError.postValue(error);
                        if (callback != null) callback.onComplete(task);
                    }
                });
    }

    /**
     * Login with email and password
     */
    public void login(String email, String password, OnCompleteListener<AuthResult> callback) {
        isLoading.postValue(true);
        clearError();

        firebaseAuth.signInWithEmailAndPassword(email.trim(), password)
                .addOnCompleteListener(task -> {
                    isLoading.postValue(false);
                    if (!task.isSuccessful()) {
                        String error = getAuthErrorMessage(task.getException());
                        authError.postValue(error);
                    }
                    if (callback != null) callback.onComplete(task);
                });
    }

    /**
     * Logout current user
     */
    public void logout() {
        firebaseAuth.signOut();
    }

    /**
     * Get current Firebase user
     */
    public FirebaseUser getFirebaseUser() {
        return firebaseAuth.getCurrentUser();
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    /**
     * Send password reset email
     */
    public void sendPasswordReset(String email, OnCompleteListener<Void> callback) {
        isLoading.postValue(true);
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    isLoading.postValue(false);
                    if (callback != null) callback.onComplete(task);
                });
    }

    private void createUserDocument(String uid, String email, String fullName, String phone,
                                    OnCompleteListener<Void> callback) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", uid);
        userData.put("email", email);
        userData.put("fullName", fullName);
        userData.put("phoneNumber", phone);
        userData.put("createdAt", System.currentTimeMillis());
        userData.put("notificationEnabled", true);
        userData.put("reminderMinutesBefore", 30);

        firestore.collection("users").document(uid)
                .set(userData)
                .addOnCompleteListener(task -> {
                    if (callback != null) callback.onComplete(task);
                });
    }

    /**
     * Get user profile from Firestore
     */
    public void getUserProfile(String uid, OnCompleteListener<DocumentSnapshot> callback) {
        firestore.collection("users").document(uid).get()
                .addOnCompleteListener(task -> {
                    if (callback != null) callback.onComplete(task);
                });
    }

    /**
     * Update user profile
     */
    public void updateUserProfile(String uid, Map<String, Object> updates, OnCompleteListener<Void> callback) {
        firestore.collection("users").document(uid)
                .update(updates)
                .addOnCompleteListener(task -> {
                    if (callback != null) callback.onComplete(task);
                });
    }

    private String getAuthErrorMessage(Exception exception) {
        if (exception == null) return "An unknown error occurred";
        String message = exception.getMessage();
        if (message == null) return "An unknown error occurred";

        if (message.contains("ERROR_INVALID_EMAIL") || message.contains("badly formatted")) {
            return "The email address is badly formatted.";
        } else if (message.contains("ERROR_WRONG_PASSWORD") || message.contains("wrong password")) {
            return "Incorrect password.";
        } else if (message.contains("ERROR_USER_NOT_FOUND") || message.contains("no user record")) {
            return "No account found with this email.";
        } else if (message.contains("ERROR_EMAIL_ALREADY_IN_USE") || message.contains("email already in use")) {
            return "This email is already registered.";
        } else if (message.contains("ERROR_WEAK_PASSWORD") || message.contains("weak password")) {
            return "Password should be at least 6 characters.";
        } else if (message.contains("ERROR_NETWORK_REQUEST_FAILED") || message.contains("network")) {
            return "Network error. Please check your connection.";
        }
        return "Authentication failed. Please try again.";
    }
}
