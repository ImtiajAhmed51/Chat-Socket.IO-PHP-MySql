package com.example.chat.ui.fragment.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chat.R;
import com.example.chat.databinding.FragmentWelcomeBinding;
import com.example.chat.ui.design.ProgressButton;

public class WelcomeFragment extends Fragment implements View.OnClickListener {

    private FragmentWelcomeBinding binding;
    private ProgressButton progressButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.emailFragmentClick.setOnClickListener(this);
        binding.loginButtonClick.setOnClickListener(this);
        this.progressButton = new ProgressButton(getContext(), binding.emailFragmentClick);
        progressButton.buttonSet("Register");

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == binding.emailFragmentClick.getId()) {
            this.binding.emailFragmentClick.setClickable(false);
            this.progressButton.buttonActivatedWithOutProgressNText();
//            this.progressButton.buttonFinished();
//            this.binding.emailFragmentClick.setClickable(false);
            Navigation.findNavController(view).navigate(R.id.action_welcomeFragment_to_emailFragment);

        } else if (view.getId() == binding.loginButtonClick.getId()) {
            Navigation.findNavController(view).navigate(R.id.action_welcomeFragment_to_loginFragment);
        }

    }
}