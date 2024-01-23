package com.example.chat.ui.fragment.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chat.R;
import com.example.chat.databinding.FragmentFriendsBinding;
import com.example.chat.databinding.FragmentProfileBinding;
import com.example.chat.utils.Constant;
import com.example.chat.utils.DimensionUtils;

public class FriendsFragment extends Fragment implements View.OnClickListener {
    private FragmentFriendsBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFriendsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Constant.setTopMargin(binding.FriendsFragmentMargin, DimensionUtils.getStatusBarHeight(requireActivity()));
        binding.friendsBackPressed.setOnClickListener(this);
        binding.addFriendsPressed.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==binding.friendsBackPressed.getId()){
            requireActivity().onBackPressed();
        } else if (view.getId()==binding.addFriendsPressed.getId()) {
            Navigation.findNavController(requireView()).navigate(R.id.action_friendsFragment_to_addFriendsFragment);
        }
    }
}