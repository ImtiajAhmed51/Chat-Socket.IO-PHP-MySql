package com.example.chat.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.chat.R;
import com.example.chat.databinding.ActivityChatUserBinding;
import com.example.chat.databinding.ActivityMainBinding;

public class ChatUserActivity extends AppCompatActivity {

    private ActivityChatUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindowColors(R.color.allBackgroundColor2);
        binding = ActivityChatUserBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
    }

    private void setWindowColors(int colorResId) {
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, colorResId));
        window.setNavigationBarColor(ContextCompat.getColor(this, colorResId));


    }
}