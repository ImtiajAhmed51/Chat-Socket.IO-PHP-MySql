package com.example.chat.ui.fragment.home;

import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.chat.R;
import com.example.chat.databinding.FragmentProfileBinding;
import com.example.chat.model.User;
import com.example.chat.ui.viewmodel.OwnViewModel;
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
import java.util.ArrayList;
import java.util.Objects;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private FragmentProfileBinding binding;
    private OwnViewModel ownViewModel;
    private BottomSheetDialog dialog;
    private ConstraintLayout[] userLayout;
    private ImageView[] userImage;
    private UserViewModel userViewModel;

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
        binding.editProfileClick.setOnClickListener(this);
        ownViewModel = new ViewModelProvider(requireActivity()).get(OwnViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        Constant.setTopMargin(binding.userProfileSettingsTopMargin, DimensionUtils.getStatusBarHeight(requireActivity()));
        Constant.setBottomMargin(binding.developerName, DimensionUtils.getNavigationBarHeight(requireActivity()));
        userLayout = new ConstraintLayout[]{binding.constraintLayoutImage1,
                binding.constraintLayoutImage2,
                binding.constraintLayoutImage3,
                binding.constraintLayoutImage4,
                binding.constraintLayoutImage5};
        userImage = new ImageView[]{binding.userImage1,
                binding.userImage2,
                binding.userImage3,
                binding.userImage4,
                binding.userImage5};
    }

    private void observeUserData() {

        userViewModel.getUserListLiveData().observe(requireActivity(), users -> {
            if (getActivity() != null) {
                int size = Math.min(users.size(), 5);
                for (int i = 0; i < size; i++) {
                    userLayout[i].setVisibility(View.VISIBLE);
                    Glide.with(requireContext()).load(Constant.getResource(users.get(i).getUserPicture())).into(userImage[i]);
                }
                for (int j = size; j < 5; j++)
                    userLayout[j].setVisibility(View.GONE);
            }
        });
        ownViewModel.getUserLiveData().observe(requireActivity(), user -> {
            if (getActivity() != null) {
                updateUI(user);
            }
        });
    }

    private void updateUI(User user) {
        Glide.with(requireActivity()).load(Constant.getResource(user.getUserPicture())).into(binding.profileUserImage);
        Glide.with(requireActivity()).load(Constant.getResource(user.getUserPicture())).into(binding.profileUserCoverImage);
        binding.userProfileDisplayName.setText(user.getUserDisplayName());
        binding.userProfileUserId.setText("#" + String.valueOf(user.getUserId()));
        binding.userProfileVerification.setVisibility(user.isUserVerified() ? View.VISIBLE : View.GONE);
        binding.userProfilePrivacy.setImageResource(user.isUserSecurity() ? R.drawable.lock : R.drawable.unlock);
        binding.userProfileUserName.setText(user.getUserName());
        binding.userProfileOwner.setVisibility(user.getUserRole().equals("ADMIN") ? View.VISIBLE : View.GONE);
        binding.userProfileDeveloper.setVisibility(user.getUserRole().equals("ADMIN") ? View.VISIBLE : View.GONE);
        binding.profileUserActiveStatus.setImageResource(Constant.getUserActiveStatusResource(user.getUserActiveStatus()));
        binding.userProfileDOBText.setText(formatDate(user.getUserDob(), "dd/MM/yyyy"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            calculateAndSetAge(user.getUserDob());
        }
        binding.userProfileOpeningDate.setText(formatDate(user.getUserAccountOpenTime(), "yyyy-MM-dd HH:mm:ss"));
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
        } else if (view.getId() == binding.userProfileLocation.getId()) {
            Navigation.findNavController(requireView()).navigate(R.id.action_profileFragment_to_locationFragment);
        } else if (view.getId() == binding.userProfileFriends.getId()) {
            Navigation.findNavController(requireView()).navigate(R.id.action_profileFragment_to_friendsFragment);
        } else if (view.getId() == binding.profileUserImageClick.getId() || view.getId() == binding.onlineStatusChangeClick.getId()) {
            showDialog();
            dialog.show();
        } else if (view.getId()==binding.editProfileClick.getId()) {
            Navigation.findNavController(requireView()).navigate(R.id.action_profileFragment_to_profileEditFragment);
        }
    }

    private void showDialog() {
        View view = getLayoutInflater().inflate(R.layout.online_status_layout, null, false);
        RadioButton online = view.findViewById(R.id.userActiveStatusOnline);
        RadioButton idle = view.findViewById(R.id.userActiveStatusIdle);
        RadioButton dND = view.findViewById(R.id.userActiveStatusDND);
        RadioButton invisible = view.findViewById(R.id.userActiveStatusInvisible);
        RadioButton[] radioButtons = {online, idle, dND, invisible};

        int checker = getUserActiveStatus(Objects.requireNonNull(ownViewModel.getUserLiveData().getValue()).getUserActiveStatus());
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
        return "";
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

    private void toastMessage(String message) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
