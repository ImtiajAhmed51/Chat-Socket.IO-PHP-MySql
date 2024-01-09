package com.example.chat.ui.fragment.home;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chat.R;
import com.example.chat.databinding.FragmentProfileBinding;
import com.example.chat.model.User;
import com.example.chat.ui.activity.AuthActivity;
import com.example.chat.ui.viewmodel.UserViewModel;
import com.example.chat.utils.Constant;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.elevation.SurfaceColors;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private UserViewModel userViewModel;
    private FragmentProfileBinding binding;
    private BottomSheetDialog dialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            binding.profileUserCoverImage.setRenderEffect(RenderEffect.createBlurEffect(100, 100, Shader.TileMode.MIRROR));
        }
        dialog = new BottomSheetDialog(requireActivity(), R.style.NoWiredStrapInNavigationBar);
        showDialog();
        binding.onlineStatusChangeClick.setOnClickListener(this);
        binding.userProfileLogout.setOnClickListener(this);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        setBottomMargin(binding.userProfileLogout, getNavigationBarHeight());
        super.onViewCreated(view, savedInstanceState);
        userViewModel.getUserLiveData().observe(requireActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (getActivity() == null) {
                    return;
                }
                Glide.with(getActivity()).load(getResource(user.getUserPicture())).into(binding.profileUserImage);
                Glide.with(getActivity()).load(getResource(user.getUserPicture())).into(binding.profileUserCoverImage);
                binding.userProfileDisplayName.setText(user.getUserDisplayName());
                binding.userProfileUserId.setText(user.getUserId());
                binding.userProfileVerification.setVisibility(user.isUserVerified() ? View.VISIBLE : View.GONE);
                binding.userProfilePrivacy.setImageResource(user.isUserSecurity() ? R.drawable.lock : R.drawable.unlock);
                binding.userProfileUserName.setText(user.getUserName());
                binding.userProfileOwner.setVisibility(user.getUserRole().equals("ADMIN") ? View.VISIBLE : View.GONE);
                binding.userProfileDeveloper.setVisibility(user.getUserRole().equals("ADMIN") ? View.VISIBLE : View.GONE);
                String formattedUserDob = user.getUserDob();
                formattedUserDob = formatDate(formattedUserDob, "dd/MM/yyyy", "MMM d, yyyy");
                binding.userProfileDOBText.setText(formattedUserDob);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    String dobString = user.getUserDob();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate dob = LocalDate.parse(dobString, formatter);
                    Period age = Period.between(dob, LocalDate.now());
                    String ageText = String.format("%d years", age.getYears());
                    if (age.getMonths() != 0) {
                        ageText += String.format(" %d months", age.getMonths());
                    }
                    if (age.getDays() != 0) {
                        ageText += String.format(" %d days", age.getDays());
                    }
                    binding.userProfileAgeText.setText(ageText);
                }
                String formattedUserAccountOpenTime = user.getUserAccountOpenTime();
                formattedUserAccountOpenTime = formatDate(formattedUserAccountOpenTime, "yyyy-MM-dd HH:mm:ss", "MMM d, yyyy");
                binding.userProfileOpeningDate.setText(formattedUserAccountOpenTime);
            }
        });
    }

    private static String formatDate(String inputDateString, String inputFormat, String outputFormat) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(inputFormat);
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(outputFormat);
            LocalDate date = LocalDate.parse(inputDateString, inputFormatter);
            return date.format(outputFormatter);
        }
        return ""; // Handle the case for lower Android versions if needed
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

    @Override
    public void onClick(View view) {
        if (view.getId() == binding.userProfileLogout.getId()) {
            Constant.clearData(getActivity());
            startActivity(new Intent(getActivity(), AuthActivity.class));
            requireActivity().finish();
        } else if (view.getId() == binding.onlineStatusChangeClick.getId()) {
            dialog.show();
        }
    }

    private void showDialog() {

        View view = getLayoutInflater().inflate(R.layout.online_status_layout, null, false);

        dialog.setContentView(view);

        dialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);


    }


}