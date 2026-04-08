package com.example.firebaseandroid.ui.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.firebaseandroid.data.model.User;
import com.example.firebaseandroid.data.repository.AuthRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class ProfileViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;
    private final MutableLiveData<User> userProfile = new MutableLiveData<>();
    private final MutableLiveData<Boolean> logoutEvent = new MutableLiveData<>();

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        authRepository = AuthRepository.getInstance();
        loadUserProfile();
    }

    public LiveData<User> getUserProfile() {
        return userProfile;
    }

    public LiveData<Boolean> getLogoutEvent() {
        return logoutEvent;
    }

    private void loadUserProfile() {
        FirebaseUser firebaseUser = authRepository.getFirebaseUser();
        if (firebaseUser == null) return;

        authRepository.getUserProfile(firebaseUser.getUid(), task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                User user = task.getResult().toObject(User.class);
                userProfile.postValue(user);
            }
        });
    }

    public void setNotificationEnabled(boolean enabled) {
        FirebaseUser firebaseUser = authRepository.getFirebaseUser();
        if (firebaseUser == null) return;

        Map<String, Object> updates = new HashMap<>();
        updates.put("notificationEnabled", enabled);
        authRepository.updateUserProfile(firebaseUser.getUid(), updates, task -> {
            if (task.isSuccessful()) {
                User current = userProfile.getValue();
                if (current != null) {
                    current.setNotificationEnabled(enabled);
                    userProfile.postValue(current);
                }
            }
        });
    }

    public void logout() {
        authRepository.logout();
        logoutEvent.postValue(true);
    }
}
