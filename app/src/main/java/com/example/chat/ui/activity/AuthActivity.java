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
import com.example.chat.databinding.ActivityAuthBinding;

public class AuthActivity extends AppCompatActivity {
    private ActivityAuthBinding binding;
    private NavHostFragment navHostFragment;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statusNavigationBarColorChange();
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
    }

    @Override
    public void onBackPressed() {

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        NavDestination currentDestination = navController.getCurrentDestination();
        int currentDestinationId = currentDestination.getId();
        if (currentDestinationId == R.id.welcomeFragment || currentDestinationId == R.id.chooseProfilePictureFragment) {
            Intent setIntent = new Intent("android.intent.action.MAIN");
            setIntent.addCategory("android.intent.category.HOME");
            setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(setIntent);
        } else {
            navController.navigateUp();
        }
    }

    private void statusNavigationBarColorChange() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.allBackgroundColor));
        window.setNavigationBarColor(getResources().getColor(R.color.allBackgroundColor));
    }
}