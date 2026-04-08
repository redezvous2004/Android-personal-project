package com.example.firebaseandroid.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.firebaseandroid.R;
import com.example.firebaseandroid.data.model.User;
import com.example.firebaseandroid.data.repository.AuthRepository;
import com.example.firebaseandroid.databinding.FragmentProfileBinding;
import com.example.firebaseandroid.ui.auth.AuthActivity;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        setupUserInfo();
        setupClickListeners();
        observeViewModel();
    }

    private void setupUserInfo() {
        FirebaseUser user = AuthRepository.getInstance().getFirebaseUser();
        if (user != null) {
            binding.tvUserEmail.setText(user.getEmail());
            binding.tvUserName.setText(user.getDisplayName() != null ?
                    user.getDisplayName() : "User");
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl())
                        .placeholder(R.drawable.bg_circle_primary)
                        .circleCrop()
                        .into(binding.ivProfile);
            }
        }
    }

    private void setupClickListeners() {
        binding.btnLogout.setOnClickListener(v -> showLogoutConfirmation());
        binding.switchReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.setNotificationEnabled(isChecked);
        });
    }

    private void observeViewModel() {
        viewModel.getUserProfile().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.tvUserName.setText(user.getFullName());
                binding.tvUserEmail.setText(user.getEmail());
                binding.tvUserPhone.setText(user.getPhoneNumber() != null ?
                        user.getPhoneNumber() : "Not set");
                binding.switchReminder.setChecked(user.isNotificationEnabled());
            }
        });

        viewModel.getLogoutEvent().observe(getViewLifecycleOwner(), loggedOut -> {
            if (loggedOut) {
                navigateToAuth();
            }
        });
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.logout))
                .setMessage(getString(R.string.logout_confirm))
                .setPositiveButton(getString(R.string.logout), (dialog, which) -> viewModel.logout())
                .setNegativeButton(getString(R.string.cancel_action), null)
                .show();
    }

    private void navigateToAuth() {
        Intent intent = new Intent(getContext(), AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
