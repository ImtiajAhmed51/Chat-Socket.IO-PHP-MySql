package com.example.chat.ui.fragment.home;

import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.chat.R;
import com.example.chat.databinding.FragmentPrivacyBinding;
import com.example.chat.databinding.FragmentProfileEditBinding;
import com.example.chat.model.User;
import com.example.chat.ui.viewmodel.OwnViewModel;
import com.example.chat.utils.Constant;
import com.example.chat.utils.DimensionUtils;

public class ProfileEditFragment extends Fragment implements View.OnClickListener{
    private FragmentProfileEditBinding binding;
    private OwnViewModel ownViewModel;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileEditBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Constant.setTopMargin(binding.profileEditTopMargin, DimensionUtils.getStatusBarHeight(requireActivity()));
        binding.profileEditBackPressed.setOnClickListener(this);
        binding.profilePictureChangeClick.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            binding.profileUserCoverImage.setRenderEffect(RenderEffect.createBlurEffect(100, 100, Shader.TileMode.MIRROR));
        }
        ownViewModel = new ViewModelProvider(requireActivity()).get(OwnViewModel.class);
        ownViewModel.getUserLiveData().observe(requireActivity(), user -> {
            if (getActivity() != null) {
                updateUI(user);
            }
        });
        binding.nameEditText.setText(ownViewModel.getUserLiveData().getValue().getUserDisplayName());
    }
    private void updateUI(User user) {
        Glide.with(requireActivity()).load(Constant.getResource(user.getUserPicture())).into(binding.profileUserImage);
        Glide.with(requireActivity()).load(Constant.getResource(user.getUserPicture())).into(binding.profileUserCoverImage);
        binding.userProfileDisplayName.setText(user.getUserDisplayName());

        binding.userProfilePrivacy.setImageResource(user.isUserSecurity() ? R.drawable.lock : R.drawable.unlock);
        binding.userProfileEditUserName.setText(user.getUserName());

        binding.profileUserActiveStatus.setImageResource(Constant.getUserActiveStatusResource(user.getUserActiveStatus()));

    }
    @Override
    public void onClick(View v) {
        if (v.getId()==binding.profileEditBackPressed.getId()){
            requireActivity().onBackPressed();
        } else if (v.getId()==binding.profilePictureChangeClick.getId()) {
            Bundle data = new Bundle();
            data.putInt("type",2);
//            data.putString("userId", userId);
//            data.putString("mainVal", mainVal);
//            data.putString("pass", password);
//            toastMessage(message + " " + name);
            Navigation.findNavController(requireView()).navigate(R.id.action_profileEditFragment_to_chooseProfilePictureFragment2, data);
        }
    }
}