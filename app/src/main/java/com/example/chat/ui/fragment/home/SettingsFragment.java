package com.example.chat.ui.fragment.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chat.databinding.FragmentSettingsBinding;
import com.example.chat.utils.Constant;
import com.example.chat.utils.DimensionUtils;

public class SettingsFragment extends Fragment implements View.OnClickListener {
    private FragmentSettingsBinding binding;

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
    }


    @Override
    public void onClick(View view) {
        if (view.getId()==binding.settingsBackPressed.getId()){
            requireActivity().onBackPressed();
        }
    }
}