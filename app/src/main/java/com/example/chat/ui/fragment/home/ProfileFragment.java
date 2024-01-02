package com.example.chat.ui.fragment.home;

import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.chat.R;
import com.example.chat.databinding.FragmentProfileBinding;
import com.example.chat.model.User;
import com.example.chat.ui.viewmodel.UserViewModel;

public class ProfileFragment extends Fragment {

    private UserViewModel userViewModel;
    private FragmentProfileBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        setBottomMargin(binding.profileFragmentMainContainer, getNavigationBarHeight());
        super.onViewCreated(view, savedInstanceState);


        userViewModel.getUserLiveData().observe(getActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (getActivity() == null) {
                    return;
                }
                Glide.with(getActivity()).load(getResource(user.getUserPicture())).into(binding.profileUserImage);
                Glide.with(getActivity()).load(getResource(user.getUserPicture())).into(binding.profileUserCoverImage);

                binding.userProfileDisplayName.setText(user.getUserDisplayName());
                binding.userProfileUserId.setText(user.getUserId());
                binding.userProfileUserName.setText(user.getUserName());

            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            binding.profileUserCoverImage.setRenderEffect(RenderEffect.createBlurEffect(100, 100, Shader.TileMode.MIRROR));
        }

    }

    private Object getResource(String userPicture) {
        switch (userPicture) {
            case "1":
                return R.drawable.frame1;
            case "2":
                return R.drawable.frame2;
            case "3":
                return R.drawable.frame3;
            case "4":
                return R.drawable.frame4;
            case "5":
                return R.drawable.frame5;
            case "6":
                return R.drawable.frame6;
            case "7":
                return R.drawable.frame7;
            case "8":
                return R.drawable.frame8;
            case "null":
                return R.drawable.logo;
            default:
                return userPicture;
        }
    }

    private int getNavigationBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void setBottomMargin(ViewGroup viewGroup, int bottomMargin) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) viewGroup.getLayoutParams();
        params.bottomMargin = bottomMargin;
        viewGroup.setLayoutParams(params);
    }
}