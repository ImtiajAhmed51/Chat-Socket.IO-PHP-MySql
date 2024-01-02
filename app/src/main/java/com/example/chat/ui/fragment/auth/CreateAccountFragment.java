package com.example.chat.ui.fragment.auth;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.chat.R;
import com.example.chat.databinding.FragmentCreateAccountBinding;
import com.example.chat.model.ValidationResult;
import com.example.chat.ui.design.ProgressButton;
import com.example.chat.utils.BackgroundWorker;
import com.example.chat.utils.EncryptionUtils;
import com.google.android.material.chip.Chip;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import static com.example.chat.utils.Constant.isPasswordValid;

public class CreateAccountFragment extends Fragment implements View.OnClickListener {

    private ProgressButton progressButton;
    private String mainVal, name, genText = "";
    private String[] genList = {"Male", "Female"};
    private Boolean userNameChecker = false;
    private FragmentCreateAccountBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCreateAccountBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mainVal = getArguments().getString("mainVal");
        name = getArguments().getString("name");
        super.onViewCreated(view, savedInstanceState);

        initializeViews();
        setupListeners();
        if (genText.equals("Male")) {
            maleSelect();
        } else if (genText.equals("Female")) {
            femaleSelect();
        }

        ArrayList<String> usernames = generateUsernames(name);
        Chip[] chips = {binding.chip1, binding.chip2, binding.chip3};
        setChipTextAndClickListeners(usernames, chips);
    }

    private void initializeViews() {
        progressButton = new ProgressButton(getContext(), binding.clickDobFragment);
        progressButton.buttonSet("Next");
    }

    private void setupListeners() {
        binding.clickDobFragment.setOnClickListener(this);
        binding.createAccountBackPressed.setOnClickListener(this);
        binding.femaleLayout.setOnClickListener(this);
        binding.maleLayout.setOnClickListener(this);

        binding.userNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String string = charSequence.toString().trim();
                if (!string.equals("")) {
                    userName(string);
                } else {
                    userNameChecker = false;
                    binding.userNameEditTextLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String inputText = charSequence.toString().trim();
                if (!inputText.equals("")) {
                    ValidationResult result = isPasswordValid(inputText);
                    binding.passwordEditTextError.setError(result.isValid() ? null : result.getMessage());
                } else {
                    binding.passwordEditTextError.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void setChipTextAndClickListeners(ArrayList<String> usernames, Chip[] chips) {
        for (int i = 0; i < chips.length; i++) {
            chips[i].setText(usernames.get(chips.length - 1 - i));
            int finalI = i;
            chips[i].setOnClickListener(v -> binding.userNameEditText.setText(chips[finalI].getText().toString().trim()));
        }
    }

    private void userName(String userName) {
        BackgroundWorker backgroundWorker = new BackgroundWorker(this::processUserNameResponse);
        try {
            backgroundWorker.execute("UserNameCheck", EncryptionUtils.encrypt(userName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processUserNameResponse(Object output) {
        try {
            JSONObject jsonResponse = new JSONObject((String) output);
            binding.userNameEditTextLayout.setErrorTextColor(ColorStateList.valueOf(getResources().getColor(jsonResponse.getBoolean("success") ? R.color.green : R.color.red)));
            binding.userNameEditTextLayout.setError(jsonResponse.getBoolean("success") ? "Available" : "UserName Use");
            userNameChecker = jsonResponse.getBoolean("success");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private ArrayList<String> generateUsernames(String fullName) {
        ArrayList<String> usernames = new ArrayList<>();
        String[] nameParts = fullName.split("\\s+");
        if (nameParts.length > 0) {
            String firstName = nameParts[0];
            String middleName = (nameParts.length > 1) ? nameParts[1] : "";
            String lastName = (nameParts.length > 1) ? nameParts[nameParts.length - 1] : "";
            usernames.add(firstName + "_" + middleName + "_" + lastName);
            usernames.add(firstName + middleName + lastName);
            usernames.add(firstName + middleName + lastName + generateRandomNumber());
        } else {
            usernames.add(fullName + generateRandomNumber());
        }
        return usernames;
    }

    private String generateRandomNumber() {
        Random rand = new Random();
        int randomNumber = rand.nextInt(10000);
        boolean includeUnderscore = rand.nextBoolean();
        return (includeUnderscore) ? "_" + randomNumber : "" + randomNumber;
    }

    @Override
    public void onClick(View view) {


        if (view.getId() == binding.clickDobFragment.getId()) {
            navigateToDobFragment();
        } else if (view.getId() == binding.createAccountBackPressed.getId()) {
            requireActivity().onBackPressed();
        } else if (view.getId() == binding.femaleLayout.getId()) {
            femaleSelect();
        } else if (view.getId() == binding.maleLayout.getId()) {
            maleSelect();
        }

    }

    private void navigateToDobFragment() {
        String userName = binding.userNameEditText.getText().toString().trim();
        String userPass = binding.passwordEditText.getText().toString().trim();
        ValidationResult result = isPasswordValid(userPass);
        if (userName.equals("") || !result.isValid() || genText.equals("") || !userNameChecker) {
            return;
        }

        binding.clickDobFragment.setClickable(false);
        progressButton.buttonActivated();
        progressButton.buttonFinished();
        binding.clickDobFragment.setClickable(false);

        Bundle data = new Bundle();
        data.putString("mainVal", mainVal);
        data.putString("name", name);
        data.putString("gen", genText);
        data.putString("userName", userName);
        data.putString("password", userPass);
        getParentFragmentManager().setFragmentResult("requestKey", data);
        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_createAccountFragment_to_dobFragment, data);
    }

    private void femaleSelect() {
        selectGender(true, false, R.color.purple_500, R.color.secondary1Color);
        genText = genList[1];
    }

    private void maleSelect() {
        selectGender(false, true, R.color.secondary1Color, R.color.purple_500);
        genText = genList[0];
    }

    private void selectGender(boolean femaleSelected, boolean maleSelected, int femaleColor, int maleColor) {
        binding.femaleLayout.setSelected(femaleSelected);
        binding.maleLayout.setSelected(maleSelected);
        binding.femaleImage.setImageResource(femaleSelected ? R.drawable.ic_female_selected : R.drawable.ic_female);
        binding.femaleText.setTextColor(getResources().getColor(femaleColor));
        binding.maleImage.setImageResource(maleSelected ? R.drawable.ic_male_selected : R.drawable.ic_male);
        binding.maleText.setTextColor(getResources().getColor(maleColor));
    }
}
