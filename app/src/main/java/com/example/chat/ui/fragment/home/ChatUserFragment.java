package com.example.chat.ui.fragment.home;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chat.databinding.FragmentChatUserBinding;
import com.example.chat.utils.Constant;

public class ChatUserFragment extends Fragment {
    private FragmentChatUserBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatUserBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle b = requireActivity().getIntent().getExtras();
        binding.chatUserDisplayName.setText(b.getString("userDisplayName"));
        binding.chatUserVerified.setVisibility(b.getBoolean("userVerified") ? View.VISIBLE : View.GONE);
        Glide.with(requireActivity()).load(Constant.getResource(b.getString("userPicture"))).into(binding.chatUserImage);
        binding.chatUserActiveStatus.setImageResource(Constant.getUserActiveStatus(b.getString("userActiveStatus")));
        b.getString("userDisplayName");
    }
    private void toastMessage(String message) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
    }
}