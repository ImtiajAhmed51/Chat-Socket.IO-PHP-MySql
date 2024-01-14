package com.example.chat.ui.fragment.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chat.databinding.FragmentLoginBinding;
import com.example.chat.model.ValidationResult;
import com.example.chat.ui.activity.MainActivity;
import com.example.chat.ui.design.ProgressButton;
import com.example.chat.utils.BackgroundWorker;
import com.example.chat.utils.Constant;
import com.example.chat.utils.EncryptionUtils;

import org.json.JSONObject;

import static com.example.chat.utils.Constant.isValidBangladeshiPhoneNumber;
import static com.example.chat.utils.Constant.isValidEmail;
import static com.example.chat.utils.Constant.isPasswordValid;

public class LoginFragment extends Fragment implements View.OnClickListener {
    private static final String LOGIN_BUTTON_TEXT = "Log In";
    private FragmentLoginBinding binding;
    private ProgressButton progressButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressButton = new ProgressButton(requireActivity(), binding.clickLoginFragment);
        progressButton.buttonSet(LOGIN_BUTTON_TEXT);
        binding.clickLoginFragment.setOnClickListener(this);
        binding.loginBackPressed.setOnClickListener(this);
        setupTextChangeListeners();
    }

    private void setupTextChangeListeners() {
        binding.loginEmailNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                handleEmailNumberTextChanged(charSequence);
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
                handlePasswordTextChanged(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void handleEmailNumberTextChanged(CharSequence charSequence) {
        if (!charSequence.toString().equals("")) {


            if (charSequence.toString().length() >= 3 &&
                    (charSequence.charAt(0) == '+' || charSequence.toString().matches("\\d+")) &&
                    (charSequence.subSequence(1, charSequence.length()).toString().matches("\\d+") ||
                            charSequence.subSequence(0, charSequence.length()).toString().matches("\\d+"))) {
                binding.loginEmailNumberPrefixTextView.setVisibility(View.VISIBLE);
                String lastTenDigits;
                String text = charSequence.toString();
                if (text.length() >= 11) {
                    if (text.startsWith("+")) {
                        lastTenDigits = text.replaceAll("[^0-9]", "").substring(2);
                    } else {
                        lastTenDigits = text.replaceAll("[^0-9]", "").substring(1);
                    }
                    binding.loginEmailNumberEditText.setText(lastTenDigits);
                    binding.loginEmailNumberEditText.setSelection(binding.loginEmailNumberEditText.getText().length());
                }

            } else {
                binding.loginEmailNumberPrefixTextView.setVisibility(View.GONE);
            }
        } else {
            binding.loginEmailNumberPrefixTextView.setVisibility(View.GONE);
        }
    }

    private void handlePasswordTextChanged(CharSequence charSequence) {
        String inputText = charSequence.toString().trim();
        if (!inputText.equals("")) {
            ValidationResult result = isPasswordValid(inputText);
            binding.loginPasswordError.setError(result.isValid() ? null : result.getMessage());
        } else {
            binding.loginPasswordError.setError(null);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == binding.clickLoginFragment.getId()) {
            handleLoginClick();
        } else if (view.getId() == binding.loginBackPressed.getId()) {
            requireActivity().onBackPressed();
        }
    }

    private void handleLoginClick() {
        String mainKey = binding.loginEmailNumberEditText.getText().toString().trim();
        String pass = binding.passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(mainKey) || TextUtils.isEmpty(pass) || !isValidInput(mainKey, pass)) {
            return;
        }

        hideKeyboard(requireActivity());
        binding.clickLoginFragment.setClickable(false);
        progressButton.buttonActivated();

        BackgroundWorker backgroundWorker = new BackgroundWorker(this::processLoginResponse);
        try {
            String emailParam = isValidEmail(mainKey) ? EncryptionUtils.encrypt(mainKey) : EncryptionUtils.encrypt("");
            String phoneNumberParam = isValidBangladeshiPhoneNumber(mainKey) ? EncryptionUtils.encrypt("0" + mainKey) : EncryptionUtils.encrypt("");
            backgroundWorker.execute("Login", emailParam, phoneNumberParam, EncryptionUtils.encrypt(pass));
        } catch (Exception e) {
            toastMessage(e.getMessage());
        }
    }

    private boolean isValidInput(String mainKey, String pass) {
        boolean isMainKeyValid = isValidEmail(mainKey) || isValidBangladeshiPhoneNumber(mainKey);
        ValidationResult result = isPasswordValid(pass);
        return isMainKeyValid && result.isValid();
    }

    private void processLoginResponse(Object output) {
        try {
            JSONObject jsonResponse = new JSONObject((String) output);
            if (jsonResponse.getBoolean("success")) {
                progressButton.buttonFinished();
                binding.loginPasswordError.setError(null);
                toastMessage(jsonResponse.getString("message") + EncryptionUtils.decrypt(jsonResponse.getString("userDisplayName")));
                String mainValue = binding.loginEmailNumberEditText.getText().toString().trim();
                String password = binding.passwordEditText.getText().toString().trim();
                mainValue = isValidEmail(mainValue) ? mainValue : "0" + mainValue;
                Constant.setDataAs(requireActivity(), EncryptionUtils.decrypt(jsonResponse.getString("userId")), mainValue, password, true);
                startMainActivity(jsonResponse.toString());
            } else {
                handleLoginErrorResponse(jsonResponse.getString("message"));
            }
        } catch (Exception e) {
            handleLoginError(e.getMessage());
        }
    }

    private void startMainActivity(String jsonObject) {
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.putExtra("jsonObject", jsonObject);
        startActivity(intent);
        requireActivity().finish();
    }

    private void handleLoginErrorResponse(String errorMessage) {
        binding.clickLoginFragment.setClickable(true);
        progressButton.buttonSet(LOGIN_BUTTON_TEXT);
        binding.loginPasswordError.setError(errorMessage);
    }

    private void handleLoginError(String errorMessage) {
        binding.clickLoginFragment.setClickable(true);
        progressButton.buttonSet(LOGIN_BUTTON_TEXT);
        toastMessage(errorMessage);
    }

    private void toastMessage(String message) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
