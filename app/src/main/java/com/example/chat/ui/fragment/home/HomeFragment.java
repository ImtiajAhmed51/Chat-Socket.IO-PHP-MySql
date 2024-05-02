package com.example.chat.ui.fragment.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chat.databinding.FragmentHomeBinding;
import com.example.chat.inter.ClickListener;
import com.example.chat.model.User;
import com.example.chat.utils.Constant;
import com.example.chat.utils.DimensionUtils;

public class HomeFragment extends Fragment implements View.OnClickListener, ClickListener {
    private FragmentHomeBinding binding;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Constant.setTopMargin(binding.homeFragmentMargin, DimensionUtils.getStatusBarHeight(requireActivity()));
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void onClickItem(User user, int position, int type,int buttonType) {

    }

    @Override
    public void onClickGalleryImage(int position, int type) {

    }
}