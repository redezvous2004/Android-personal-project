package com.example.firebaseandroid.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.firebaseandroid.data.repository.AuthRepository;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends ViewModel {

    private final AuthRepository repository;
    private final MutableLiveData<Boolean> isLoginMode = new MutableLiveData<>(true);
    private final MutableLiveData<Boolean> authSuccess = new MutableLiveData<>();

    public AuthViewModel() {
        repository = AuthRepository.getInstance();
    }

    public LiveData<FirebaseUser> getCurrentUser() {
        return repository.getCurrentUser();
    }

    public LiveData<Boolean> getIsLoading() {
        return repository.getIsLoading();
    }

    public LiveData<String> getAuthError() {
        return repository.getAuthError();
    }

    public LiveData<Boolean> getIsLoginMode() {
        return isLoginMode;
    }

    public LiveData<Boolean> getAuthSuccess() {
        return authSuccess;
    }

    public void setLoginMode(boolean isLogin) {
        isLoginMode.setValue(isLogin);
    }

    public void login(String email, String password) {
        repository.login(email, password, task -> {
            if (task.isSuccessful()) {
                authSuccess.postValue(true);
            }
        });
    }

    public void register(String email, String password, String fullName, String phone) {
        repository.register(email, password, fullName, phone, task -> {
            if (task.isSuccessful()) {
                authSuccess.postValue(true);
            }
        });
    }

    public void clearError() {
        repository.clearError();
    }
}
