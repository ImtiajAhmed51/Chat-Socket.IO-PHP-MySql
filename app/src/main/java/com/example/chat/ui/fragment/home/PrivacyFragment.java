package com.example.chat.ui.fragment.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.chat.databinding.FragmentPrivacyBinding;
import com.example.chat.model.User;
import com.example.chat.ui.viewmodel.OwnViewModel;
import com.example.chat.utils.BackgroundWorker;
import com.example.chat.utils.Constant;
import com.example.chat.utils.DimensionUtils;
import com.example.chat.utils.EncryptionUtils;

import org.json.JSONObject;

public class PrivacyFragment extends Fragment implements View.OnClickListener {

    private FragmentPrivacyBinding binding;
    private RadioButton[] privacyRadio;
    private OwnViewModel ownViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPrivacyBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Constant.setTopMargin(binding.privacyFragmentTopMargin, DimensionUtils.getStatusBarHeight(requireActivity()));

        Constant.setBottomMargin(binding.privacyFragmentBottomMargin, DimensionUtils.getNavigationBarHeight(requireActivity()));
        privacyRadio = new RadioButton[]{binding.radioPrivacyModeOn,
                binding.radioPrivacyModeOff};
        ownViewModel = new ViewModelProvider(requireActivity()).get(OwnViewModel.class);
        ownViewModel.getUserLiveData().observe(requireActivity(), user -> {
            if (getActivity() != null) {
                updateUI(user);
            }
        });
        binding.privacyBackPressed.setOnClickListener(this);
    }

    private void updateUI(User user) {
        if (user.isUserSecurity()) {
            privacyRadio[0].setChecked(true);
            privacyRadio[1].setChecked(false);
        } else {
            privacyRadio[0].setChecked(false);
            privacyRadio[1].setChecked(true);
        }
        for (int i = 0; i < privacyRadio.length; i++) {
            final int index = i;
            privacyRadio[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean checked = ((RadioButton) view).isChecked();
                    if (checked) {
                        userPrivacyModeChange(getUserPrivacyModeValue(index));
                        modeRadioButtonsState(privacyRadio, index);
                    }
                }
            });
        }


    }

    private String getUserPrivacyModeValue(int index) {
        switch (index) {
            case 0:
                return "Yes";
            case 1:
                return "No";
            default:
                return "";
        }
    }


    private void modeRadioButtonsState(RadioButton[] radioButtons, int selectedIndex) {
        for (int i = 0; i < radioButtons.length; i++) {
            radioButtons[i].setChecked(i == selectedIndex);
        }
    }

    private void userPrivacyModeChange(String status) {
        BackgroundWorker backgroundWorker = new BackgroundWorker(this::userPrivacyModeChangeResponse);
        try {
            String userId = Constant.getDataId(requireActivity());
            backgroundWorker.execute("UserPrivacyMode", EncryptionUtils.encrypt(userId), status);
        } catch (Exception e) {
            toastMessage(e.getMessage());
        }
    }

    private void userPrivacyModeChangeResponse(Object output) {
        try {
            JSONObject jsonResponse = new JSONObject((String) output);
            if (jsonResponse.getBoolean("success")) {
                //dialog.dismiss();
            }
        } catch (Exception e) {
            toastMessage(e.getMessage());
        }
    }

    private void toastMessage(String message) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if (binding.privacyBackPressed.getId() == v.getId()) {
            requireActivity().onBackPressed();
        }
    }
}