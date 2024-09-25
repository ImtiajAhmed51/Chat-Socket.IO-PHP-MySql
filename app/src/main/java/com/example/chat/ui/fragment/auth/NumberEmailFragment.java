package com.example.chat.ui.fragment.auth;

import static com.example.chat.utils.Constant.isValidBangladeshiPhoneNumber;
import static com.example.chat.utils.Constant.isValidEmail;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.chat.R;
import com.example.chat.databinding.FragmentNumberEmailBinding;
import com.example.chat.ui.design.ProgressButton;
import com.example.chat.ui.viewmodel.NumberEmailViewModel;
import com.example.chat.utils.BackgroundWorker;
import com.example.chat.utils.Constant;
import com.example.chat.utils.EncryptionUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class NumberEmailFragment extends Fragment implements View.OnClickListener {
    private ProgressButton progressButton;
    private FragmentNumberEmailBinding binding;
    private NumberEmailViewModel numberEmailViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNumberEmailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModel();
        setupListeners();
        setupFragmentResultListener();
        setupInitialUIState();
    }

    private void initViewModel() {
        progressButton = new ProgressButton(requireActivity(), binding.clickNameFragment);
        progressButton.buttonSet("Next");
        numberEmailViewModel = new ViewModelProvider(requireActivity()).get(NumberEmailViewModel.class);
    }

    private void setupListeners() {
        binding.clickNameFragment.setOnClickListener(this);
        binding.enterNumberEmailBackPressed.setOnClickListener(this);
        binding.numberEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validateInput(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        binding.numberRadio.setOnClickListener(view -> setMode(NumberEmailViewModel.MODE_NUMBER));
        binding.emailRadio.setOnClickListener(view -> setMode(NumberEmailViewModel.MODE_EMAIL));
    }

    private void setupFragmentResultListener() {
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, result) -> {
            if (result.containsKey("mainVal")) {
                binding.numberEmailEditText.setText(result.getString("mainVal"));
            }
        });
    }

    private void setupInitialUIState() {
        int checkMode = numberEmailViewModel.getCheck();
        resetUIState();
        setMode(checkMode);
    }

    private void resetUIState() {
        binding.numberEmailEditText.setText("");
        binding.numberEmailEditTextLayout.setError(null);
    }

    private void setMode(int mode) {
        if (numberEmailViewModel.getCheck() != mode) {
            resetUIState();
        }
        numberEmailViewModel.setCheck(mode);
        if (mode == NumberEmailViewModel.MODE_NUMBER) {
            binding.headTitle.setText("NUMBER");
            binding.prefixTextView.setVisibility(View.VISIBLE);
            binding.numberEmailEditText.setHint("Number");
            binding.numberEmailEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
            binding.numberRadio.setChecked(true);
        } else if (mode == NumberEmailViewModel.MODE_EMAIL) {
            binding.headTitle.setText("EMAIL");
            binding.prefixTextView.setVisibility(View.GONE);
            binding.numberEmailEditText.setHint("Email");
            binding.numberEmailEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            binding.emailRadio.setChecked(true);
        }
    }

    private void validateInput(String inputText) {
        if (!inputText.equals("")) {
            if (numberEmailViewModel.getCheck() == NumberEmailViewModel.MODE_NUMBER) {

                String lastTenDigits;
                String text = inputText.toString();
                if (text.length() >= 11) {
                    if (text.startsWith("+")) {
                        lastTenDigits = text.replaceAll("[^0-9]", "").substring(2);
                    } else {
                        lastTenDigits = text.replaceAll("[^0-9]", "").substring(1);
                    }
                    binding.numberEmailEditText.setText(lastTenDigits);
                    binding.numberEmailEditText.setSelection(binding.numberEmailEditText.getText().length());
                }


                binding.numberEmailEditTextLayout.setError(isValidBangladeshiPhoneNumber(inputText) ? null : "Enter a valid phone number.");
            } else if (numberEmailViewModel.getCheck() == NumberEmailViewModel.MODE_EMAIL) {
                binding.numberEmailEditTextLayout.setError(isValidEmail(inputText) ? null : "Enter a valid email.");
            }
        } else {
            binding.numberEmailEditTextLayout.setError(null);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == binding.clickNameFragment.getId()) {
            handleNextButtonClick();
        } else if (view.getId() == binding.enterNumberEmailBackPressed.getId()) {
            requireActivity().onBackPressed();
        }
    }

    private void handleNextButtonClick() {
        String emailNumber = binding.numberEmailEditText.getText().toString().trim();
        if (numberEmailViewModel.getCheck() == NumberEmailViewModel.MODE_NUMBER && !Constant.isValidBangladeshiPhoneNumber(emailNumber)) {
            return;
        } else if (numberEmailViewModel.getCheck() == NumberEmailViewModel.MODE_EMAIL && !isValidEmail(emailNumber)) {
            return;
        }
        binding.clickNameFragment.setClickable(false);
        progressButton.buttonActivated();
        BackgroundWorker backgroundWorker = new BackgroundWorker(this::processEmailNumberCheckResponse);
        try {
            String encryptedEmail = isValidEmail(emailNumber) ? EncryptionUtils.encrypt(emailNumber) : EncryptionUtils.encrypt("");
            String encryptedPhoneNumber = isValidBangladeshiPhoneNumber(emailNumber) ? EncryptionUtils.encrypt("0" + emailNumber) : EncryptionUtils.encrypt("");
            backgroundWorker.execute("EmailNumberCheck", encryptedEmail, encryptedPhoneNumber);
        } catch (Exception e) {
            binding.clickNameFragment.setClickable(true);
            progressButton.buttonSet("Next");
            toastMessage(e.getMessage());
        }
    }

    private void processEmailNumberCheckResponse(Object object) {
        try {

            JSONObject jsonResponse = new JSONObject((String) object);
            if (jsonResponse.getBoolean("success")) {
                progressButton.buttonFinished();
                Bundle data = new Bundle();
                binding.numberEmailEditTextLayout.setError(null);
                String mainVal = binding.numberEmailEditText.getText().toString().trim();
                mainVal = isValidEmail(mainVal) ? mainVal : "0" + mainVal;
                data.putString("mainVal", mainVal);
                getParentFragmentManager().setFragmentResult("requestKey", data);
                Navigation.findNavController(requireView()).navigate(R.id.action_numberEmailFragment_to_nameFragment, data);
            } else {
                binding.numberEmailEditTextLayout.setError(jsonResponse.getString("message"));
                binding.clickNameFragment.setClickable(true);
                progressButton.buttonSet("Next");
            }
        } catch (Exception ex) {
            binding.clickNameFragment.setClickable(true);
            progressButton.buttonSet("Next");
            toastMessage(ex.getMessage());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.clickNameFragment.setClickable(true);
        progressButton.buttonSet("Next");
    }

    private void toastMessage(String message) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
