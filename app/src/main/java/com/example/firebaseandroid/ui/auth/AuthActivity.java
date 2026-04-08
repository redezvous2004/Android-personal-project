package com.example.firebaseandroid.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.firebaseandroid.databinding.ActivityAuthBinding;
import com.example.firebaseandroid.ui.main.MainActivity;

public class AuthActivity extends AppCompatActivity {

    private ActivityAuthBinding binding;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        viewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                navigateToMain();
            }
        });

        setupViews();
        observeViewModel();
    }

    private void setupViews() {
        // Switch between Login and Register
        binding.tvSwitchMode.setText(getString(com.example.firebaseandroid.R.string.dont_have_account)
                + " ");
        binding.tvSwitchModeAction.setText(getString(com.example.firebaseandroid.R.string.sign_up_here));

        binding.tvSwitchModeAction.setOnClickListener(v -> {
            boolean isLogin = viewModel.getIsLoginMode().getValue() != null && viewModel.getIsLoginMode().getValue();
            viewModel.setLoginMode(!isLogin);
        });

        viewModel.getIsLoginMode().observe(this, this::updateModeUI);

        // Login button
        binding.btnAction.setText(getString(com.example.firebaseandroid.R.string.login));
        binding.btnAction.setOnClickListener(v -> performAuth());
    }

    private void updateModeUI(boolean isLogin) {
        if (isLogin) {
            binding.tvTitle.setText(getString(com.example.firebaseandroid.R.string.login));
            binding.tilFullName.setVisibility(View.GONE);
            binding.tilPhone.setVisibility(View.GONE);
            binding.tilConfirmPassword.setVisibility(View.GONE);
            binding.tvSwitchMode.setText(getString(com.example.firebaseandroid.R.string.dont_have_account) + " ");
            binding.tvSwitchModeAction.setText(getString(com.example.firebaseandroid.R.string.sign_up_here));
            binding.btnAction.setText(getString(com.example.firebaseandroid.R.string.login));
        } else {
            binding.tvTitle.setText(getString(com.example.firebaseandroid.R.string.register));
            binding.tilFullName.setVisibility(View.VISIBLE);
            binding.tilPhone.setVisibility(View.VISIBLE);
            binding.tilConfirmPassword.setVisibility(View.VISIBLE);
            binding.tvSwitchMode.setText(getString(com.example.firebaseandroid.R.string.already_have_account) + " ");
            binding.tvSwitchModeAction.setText(getString(com.example.firebaseandroid.R.string.sign_in_here));
            binding.btnAction.setText(getString(com.example.firebaseandroid.R.string.register));
        }
        clearErrors();
    }

    private void performAuth() {
        clearErrors();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString();
        String fullName = binding.etFullName.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString();

        boolean isLogin = viewModel.getIsLoginMode().getValue() != null && viewModel.getIsLoginMode().getValue();

        // Validate
        String emailError = com.example.firebaseandroid.util.ValidationUtils.validateEmail(email);
        String passwordError = com.example.firebaseandroid.util.ValidationUtils.validatePassword(password);
        if (emailError != null) {
            binding.tilEmail.setError(emailError);
            return;
        }
        if (passwordError != null) {
            binding.tilPassword.setError(passwordError);
            return;
        }

        if (isLogin) {
            viewModel.login(email, password);
        } else {
            String nameError = com.example.firebaseandroid.util.ValidationUtils.validateFullName(fullName);
            String phoneError = com.example.firebaseandroid.util.ValidationUtils.validatePhone(phone);
            String confirmError = com.example.firebaseandroid.util.ValidationUtils.validateConfirmPassword(password, confirmPassword);

            if (nameError != null) {
                binding.tilFullName.setError(nameError);
                return;
            }
            if (phoneError != null) {
                binding.tilPhone.setError(phoneError);
                return;
            }
            if (confirmError != null) {
                binding.tilConfirmPassword.setError(confirmError);
                return;
            }
            viewModel.register(email, password, fullName, phone);
        }
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnAction.setEnabled(!isLoading);
        });

        viewModel.getAuthError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getAuthSuccess().observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(this, getString(com.example.firebaseandroid.R.string.login_success), Toast.LENGTH_SHORT).show();
                navigateToMain();
            }
        });
    }

    private void clearErrors() {
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);
        binding.tilFullName.setError(null);
        binding.tilPhone.setError(null);
        binding.tilConfirmPassword.setError(null);
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
