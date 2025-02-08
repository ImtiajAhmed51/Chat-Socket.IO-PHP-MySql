package com.example.chat.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
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
    @Override
    public void onBackPressed() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_chat_host_fragment);
        NavController navController = navHostFragment.getNavController();
        NavDestination currentDestination = navController.getCurrentDestination();
        int currentDestinationId = currentDestination.getId();
        if (currentDestinationId == R.id.chatUserFragment) {
            this.finish();
        } else {
            navController.navigateUp();
        }
    }
}