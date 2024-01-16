package com.example.chat.ui.fragment.home;

import android.content.Intent;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.chat.R;
import com.example.chat.adapter.CustomAddUserAdapter;
import com.example.chat.databinding.FragmentProfileBinding;
import com.example.chat.model.User;
import com.example.chat.ui.activity.AuthActivity;
import com.example.chat.ui.viewmodel.UserViewModel;
import com.example.chat.utils.BackgroundWorker;
import com.example.chat.utils.Constant;
import com.example.chat.utils.DimensionUtils;
import com.example.chat.utils.EncryptionUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONObject;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private FragmentProfileBinding binding;
    private UserViewModel userViewModel;
    private BottomSheetDialog dialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initUI();
        observeUserData();
    }

    private void initUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            binding.profileUserCoverImage.setRenderEffect(RenderEffect.createBlurEffect(100, 100, Shader.TileMode.MIRROR));
        }
        dialog = new BottomSheetDialog(requireActivity(), R.style.NoWiredStrapInNavigationBar);
        binding.onlineStatusChangeClick.setOnClickListener(this);
        binding.userProfileLocation.setOnClickListener(this);
        binding.profileUserImageClick.setOnClickListener(this);

        binding.userProfileSettings.setOnClickListener(this);
        binding.userProfileFriends.setOnClickListener(this);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        Constant.setTopMargin(binding.userProfileSettingsTopMargin,DimensionUtils.getStatusBarHeight(requireActivity()));
        Constant.setBottomMargin(binding.developerName, DimensionUtils.getNavigationBarHeight(requireActivity()));
    }

    private void observeUserData() {
        userViewModel.getUserLiveData().observe(requireActivity(), user -> {
            if (getActivity() == null) {
                return;
            }
            updateUI(user);
        });
    }

    private void updateUI(User user) {
        Glide.with(requireActivity()).load(getResource(user.getUserPicture())).into(binding.profileUserImage);
        Glide.with(requireActivity()).load(getResource(user.getUserPicture())).into(binding.profileUserCoverImage);
        binding.userProfileDisplayName.setText(user.getUserDisplayName());
        binding.userProfileUserId.setText(user.getUserId());
        binding.userProfileVerification.setVisibility(user.isUserVerified() ? View.VISIBLE : View.GONE);
        binding.userProfilePrivacy.setImageResource(user.isUserSecurity() ? R.drawable.lock : R.drawable.unlock);
        binding.userProfileUserName.setText(user.getUserName());
        binding.userProfileOwner.setVisibility(user.getUserRole().equals("ADMIN") ? View.VISIBLE : View.GONE);
        binding.userProfileDeveloper.setVisibility(user.getUserRole().equals("ADMIN") ? View.VISIBLE : View.GONE);
        binding.profileUserActiveStatus.setImageResource(getUserActiveStatusResource(user.getUserActiveStatus()));
        String formattedUserDob = formatDate(user.getUserDob(), "dd/MM/yyyy");
        binding.userProfileDOBText.setText(formattedUserDob);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            calculateAndSetAge(user.getUserDob());
        }
        String formattedUserAccountOpenTime = formatDate(user.getUserAccountOpenTime(), "yyyy-MM-dd HH:mm:ss");
        binding.userProfileOpeningDate.setText(formattedUserAccountOpenTime);
        showDialog();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void calculateAndSetAge(String dobString) {
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

    @Override
    public void onClick(View view) {
        if (view.getId() == binding.userProfileSettings.getId()) {
            Navigation.findNavController(requireView()).navigate(R.id.action_profileFragment_to_settingsFragment);
        }
        else if (view.getId() == binding.onlineStatusChangeClick.getId()) {
            showDialog();
            dialog.show();
        } else if (view.getId() ==binding.userProfileLocation.getId()) {
            Navigation.findNavController(requireView()).navigate(R.id.action_profileFragment_to_locationFragment);
        }else if (view.getId()==binding.userProfileFriends.getId()){
            Navigation.findNavController(requireView()).navigate(R.id.action_profileFragment_to_friendsFragment);
        }else if (view.getId()==binding.profileUserImageClick.getId()){
            showDialog();
            dialog.show();
        }
    }



    private void showDialog() {
        View view = getLayoutInflater().inflate(R.layout.online_status_layout, null, false);
        RadioButton online = view.findViewById(R.id.userActiveStatusOnline);
        RadioButton idle = view.findViewById(R.id.userActiveStatusIdle);
        RadioButton dND = view.findViewById(R.id.userActiveStatusDND);
        RadioButton invisible = view.findViewById(R.id.userActiveStatusInvisible);
        RadioButton[] radioButtons = {online, idle, dND, invisible};

        int checker = getUserActiveStatus(Objects.requireNonNull(userViewModel.getUserLiveData().getValue()).getUserActiveStatus());
        for (int i = 0; i < radioButtons.length; i++) {
            radioButtons[i].setChecked(checker == i);
        }

        for (int i = 0; i < radioButtons.length; i++) {
            final int index = i;
            radioButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean checked = ((RadioButton) view).isChecked();
                    if (checked) {
                        userActiveStatusChange(getUserActiveStatusValue(index));
                        updateRadioButtonsState(radioButtons, index);
                    }
                }
            });
        }

        dialog.setContentView(view);
        dialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
    }
    private void updateRadioButtonsState(RadioButton[] radioButtons, int selectedIndex) {
        for (int i = 0; i < radioButtons.length; i++) {
            radioButtons[i].setChecked(i == selectedIndex);
        }
    }
    private String getUserActiveStatusValue(int index) {
        switch (index) {
            case 0:
                return "Online";
            case 1:
                return "Idle";
            case 2:
                return "DND";
            case 3:
                return "Invisible";
            default:
                return "";
        }
    }

    private void userActiveStatusChange(String status) {
        BackgroundWorker backgroundWorker = new BackgroundWorker(this::userActiveStatusChangeResponse);
        try {
            String userId = Constant.getDataId(requireActivity());
            backgroundWorker.execute("UserActiveStatusChange", EncryptionUtils.encrypt(userId), status);
        } catch (Exception e) {
            toastMessage(e.getMessage());
        }
    }

    private void userActiveStatusChangeResponse(Object output) {
        try {
            JSONObject jsonResponse = new JSONObject((String) output);
            if (jsonResponse.getBoolean("success")) {
                //dialog.dismiss();
            }
        } catch (Exception e) {
            toastMessage(e.getMessage());
        }
    }

    private static String formatDate(String inputDateString, String inputFormat) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(inputFormat);
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
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

    private int getUserActiveStatus(String userPicture) {
        switch (userPicture) {
            case "Online":
                return 0;
            case "Idle":
                return 1;
            case "DND":
                return 2;
            case "Invisible":
                return 3;
        }
        return -1;
    }

    private int getUserActiveStatusResource(String userPicture) {
        switch (userPicture) {
            case "Online":
                return R.drawable.online;
            case "Idle":
                return R.drawable.idle;
            case "DND":
                return R.drawable.dnd;
            case "Invisible":
                return R.drawable.invisible;
        }
        return R.drawable.circular_border;
    }

    private void toastMessage(String message) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
