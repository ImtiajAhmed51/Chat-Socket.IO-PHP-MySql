package com.example.chat.ui.fragment.home;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.example.chat.R;
import com.example.chat.databinding.FragmentSettingsBinding;
import com.example.chat.ui.activity.AuthActivity;
import com.example.chat.utils.Constant;
import com.example.chat.utils.DimensionUtils;

public class SettingsFragment extends Fragment implements View.OnClickListener {
    private FragmentSettingsBinding binding;
    private Dialog customDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Constant.setTopMargin(binding.settingsFragmentMargin, DimensionUtils.getStatusBarHeight(requireActivity()));

        binding.settingsBackPressed.setOnClickListener(this);
        binding.userSettingsLogOut.setOnClickListener(this);
        dialogCreate();
    }

    private void dialogCreate() {
        customDialog = new Dialog(requireActivity());
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setContentView(R.layout.logout_dialog);


        AppCompatButton logOut = customDialog.findViewById(R.id.logOutClick);
        AppCompatButton cancelLogOut = customDialog.findViewById(R.id.cancelLogOutClick);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });
        cancelLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
            }
        });
        customDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        customDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        customDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == binding.settingsBackPressed.getId()) {
            requireActivity().onBackPressed();
        } else if (view.getId() == binding.userSettingsLogOut.getId()) {
            if (customDialog.isShowing()) {
                return;
            }
            customDialog.show();
        }
    }

    private void logoutUser() {
        customDialog.dismiss();
        Constant.clearData(requireActivity());
        startActivity(new Intent(requireActivity(), AuthActivity.class));
        requireActivity().finish();
    }
}