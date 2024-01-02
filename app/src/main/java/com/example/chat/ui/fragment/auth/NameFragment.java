package com.example.chat.ui.fragment.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.chat.R;
import com.example.chat.databinding.FragmentNameBinding;
import com.example.chat.ui.design.ProgressButton;

public class NameFragment extends Fragment implements View.OnClickListener {

    private ProgressButton progressButton;
    private String mainVal;
    private FragmentNameBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentNameBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        assert getArguments() != null;
        mainVal = getArguments().getString("mainVal");
        super.onViewCreated(view, savedInstanceState);
        binding.clickCreateAccountFragment.setOnClickListener(this);
        binding.nameBackPressed.setOnClickListener(this);
        this.progressButton = new ProgressButton(getContext(), binding.clickCreateAccountFragment);
        progressButton.buttonSet("Next");
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, result) -> {
            if (result.containsKey("name")) {
                String receivedData = result.getString("name");
                binding.nameEditText.setText(receivedData);
            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == binding.clickCreateAccountFragment.getId()) {
            this.binding.clickCreateAccountFragment.setClickable(false);
            this.progressButton.buttonActivated();
            this.progressButton.buttonFinished();
            this.binding.clickCreateAccountFragment.setClickable(false);
            Bundle data = new Bundle();
            data.putString("mainVal", mainVal);
            data.putString("name", binding.nameEditText.getText().toString().trim());

            getParentFragmentManager().setFragmentResult("requestKey", data);
            Navigation.findNavController(view).navigate(R.id.action_nameFragment_to_createAccountFragment, data);

        } else if (view.getId() == binding.nameBackPressed.getId()) {
            requireActivity().onBackPressed();

        }
    }
}