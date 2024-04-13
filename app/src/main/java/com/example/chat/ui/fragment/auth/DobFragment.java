package com.example.chat.ui.fragment.auth;
import static com.example.chat.utils.Constant.isValidBangladeshiPhoneNumber;
import static com.example.chat.utils.Constant.isValidEmail;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.chat.R;
import com.example.chat.databinding.FragmentDobBinding;
import com.example.chat.ui.design.ProgressButton;
import com.example.chat.utils.BackgroundWorker;
import com.example.chat.utils.EncryptionUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class DobFragment extends Fragment implements View.OnClickListener {
    private FragmentDobBinding binding;
    private ProgressButton progressButton;
    private String name="", mainVal="", userName="", password="", gen="", dob="";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDobBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeArguments();
        initializeViews();
    }

    private void initializeArguments() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mainVal = arguments.getString("mainVal");
            name = arguments.getString("name");
            userName = arguments.getString("userName");
            gen = arguments.getString("gen");
            password = arguments.getString("password");
        }
    }

    private void initializeViews() {
        progressButton = new ProgressButton(requireActivity(), binding.clickProfilePictureFragment);
        progressButton.buttonSet("Create an account");

        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        binding.dobEditText.setHint(date);
        binding.dobEditText.setOnClickListener(this);

        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, result) -> {
            if (result.containsKey("dob")) {
                binding.dobEditText.setText(result.getString("dob"));
            }
        });

        binding.clickProfilePictureFragment.setOnClickListener(this);
        binding.dobBackPressed.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==binding.dobEditText.getId()){
            showDatePicker();
        } else if (view.getId()==binding.clickProfilePictureFragment.getId()) {
            handleProfilePictureClick();
        } else if (view.getId()==binding.dobBackPressed.getId()) {
            requireActivity().onBackPressed();
        }
    }

    private void showDatePicker() {
        Calendar minAgeCalendar = Calendar.getInstance();
        minAgeCalendar.add(Calendar.YEAR, -18);
        Calendar currentDate = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(), (view, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);
            if (selectedDate.before(minAgeCalendar)) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                binding.dobEditText.setText(sdf.format(selectedDate.getTime()));
                dob = sdf.format(selectedDate.getTime());
            } else {
                dob = "";
                binding.dobEditText.setText("");
                toastMessage("Minimum Age Requirement 18.");
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(minAgeCalendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void handleProfilePictureClick() {

        if (dob.equals("")){
            return;
        }
        binding.clickProfilePictureFragment.setClickable(false);
        progressButton.buttonActivated();
        Random random = new Random();
        String userId = String.valueOf((1000 + random.nextInt(9000)));
        BackgroundWorker backgroundWorker = new BackgroundWorker(this::processCreateAccountResponse);

        try {
            String emailParam = isValidEmail(mainVal) ? EncryptionUtils.encrypt(mainVal) : EncryptionUtils.encrypt("");
            String phoneNumberParam = isValidBangladeshiPhoneNumber(mainVal) ? EncryptionUtils.encrypt(mainVal) : EncryptionUtils.encrypt("");
            backgroundWorker.execute(
                    "CreateAnAccount",
                    EncryptionUtils.encrypt(userId),
                    EncryptionUtils.encrypt(name),
                    EncryptionUtils.encrypt(userName),
                    EncryptionUtils.encrypt(dob),
                    emailParam,
                    phoneNumberParam,
                    gen,
                    EncryptionUtils.encrypt(password)
            );
        } catch (Exception e) {
            binding.clickProfilePictureFragment.setClickable(true);
            progressButton.buttonSet("Create an account");
            toastMessage(e.getMessage());
        }
    }

    private void processCreateAccountResponse(Object output) {
        try {
            JSONObject jsonResponse = new JSONObject((String) output);
            if (jsonResponse.getBoolean("success")) {
                progressButton.buttonFinished();
                String message = jsonResponse.getString("message");
                String name = EncryptionUtils.decrypt(jsonResponse.getString("name"));
                String userId =EncryptionUtils.decrypt(jsonResponse.getString("userId"));
                Bundle data = new Bundle();
                data.putString("userId", userId);
                data.putString("mainVal", mainVal);
                data.putString("pass", password);
                toastMessage(message+" "+name);
                Navigation.findNavController(requireView()).navigate(R.id.action_dobFragment_to_chooseProfilePictureFragment, data);
            } else {
                binding.clickProfilePictureFragment.setClickable(true);
                progressButton.buttonSet("Create an account");
                toastMessage(jsonResponse.getString("message"));
            }
        } catch (JSONException e) {
            handleResponseError(e.getMessage());
        } catch (Exception ex) {
            handleResponseError(ex.getMessage());
        }
    }

    private void handleResponseError(String errorMessage) {
        binding.clickProfilePictureFragment.setClickable(true);
        progressButton.buttonSet("Create an account");
        toastMessage(errorMessage);
    }

    private void toastMessage(String message) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
