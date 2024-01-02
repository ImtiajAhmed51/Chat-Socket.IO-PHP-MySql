package com.example.chat.ui.activity;

import static com.example.chat.utils.Constant.isValidEmail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.chat.R;
import com.example.chat.databinding.ActivitySplashBinding;
import com.example.chat.utils.BackgroundWorker;
import com.example.chat.utils.Constant;
import com.example.chat.utils.EncryptionUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statusNavigationBarColorChange();
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Constant.getDataLogin(SplashActivity.this)) {
                    final String mainKey = Constant.getDataMainKey(SplashActivity.this);
                    final String pass = Constant.getDataPass(SplashActivity.this);
                    BackgroundWorker backgroundWorker = new BackgroundWorker(new BackgroundWorker.AsyncResponse() {
                        public void onFinish(Object output) {
                            try {
                                JSONObject jsonResponse = new JSONObject((String) output);
                                if (jsonResponse.getBoolean("success")) {
                                    try {
                                        Constant.setDataAs(SplashActivity.this, EncryptionUtils.decrypt(jsonResponse.getString("userId")), mainKey, pass, true);
                                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                        intent.putExtra("jsonObject", jsonResponse.toString());
                                        startActivity(intent);
                                        finish();
                                        return;
                                    } catch (Exception ex) {

                                    }
                                }
                                Constant.clearData(SplashActivity.this);
                                toastMessage(jsonResponse.getString("message"));
                                startActivity(new Intent(SplashActivity.this, AuthActivity.class));
                                finish();
                            } catch (JSONException e) {
                                toastMessage(e.getMessage().toString().trim());
                                e.printStackTrace();
                                startActivity(new Intent(SplashActivity.this, AuthActivity.class));
                                finish();
                            }
                        }
                    });
                    try {
                        if (isValidEmail(mainKey)) {
                            backgroundWorker.execute("Login", EncryptionUtils.encrypt(mainKey), EncryptionUtils.encrypt(""), EncryptionUtils.encrypt(pass));
                        } else {
                            backgroundWorker.execute("Login", EncryptionUtils.encrypt(""), EncryptionUtils.encrypt(mainKey), EncryptionUtils.encrypt(pass));
                        }
                    } catch (Exception ex) {
                        toastMessage(ex.getMessage());
                    }

                } else {
                    Constant.clearData(SplashActivity.this);
                    startActivity(new Intent(SplashActivity.this, AuthActivity.class));
                    finish();
                }
            }
        }, 1500);
    }

    private void statusNavigationBarColorChange() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.allBackgroundColor2));
        window.setNavigationBarColor(getResources().getColor(R.color.allBackgroundColor2));
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    private void toastMessage(String message) {
        Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}