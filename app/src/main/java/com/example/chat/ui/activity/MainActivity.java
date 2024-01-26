package com.example.chat.ui.activity;
import java.lang.reflect.Type;

import static com.example.chat.utils.Constant.isValidBangladeshiPhoneNumber;
import static com.example.chat.utils.Constant.isValidEmail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.chat.R;
import com.example.chat.databinding.ActivityMainBinding;
import com.example.chat.model.User;
import com.example.chat.ui.fragment.home.AddUserFragment;
import com.example.chat.ui.fragment.home.HomeFragment;
import com.example.chat.ui.fragment.home.UserDrawerFragment;
import com.example.chat.ui.viewmodel.AddUserViewModel;
import com.example.chat.ui.viewmodel.FragmentViewModel;
import com.example.chat.ui.viewmodel.UserViewModel;
import com.example.chat.utils.BackgroundWorker;
import com.example.chat.utils.Constant;
import com.example.chat.utils.EncryptionUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private UserViewModel userViewModel;
    private AddUserViewModel addUserViewModel;
    private static final long REFRESH_INTERVAL = 2000;
    private Handler handler;
    private Set<User> userSet = new HashSet<>();
    private final ArrayList<User> userList = new ArrayList<>();
    private Runnable refreshDataRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String jsonString = getIntent().getStringExtra("jsonObject");
        try {
            userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
            addUserViewModel = new ViewModelProvider(this).get(AddUserViewModel.class);
            FragmentViewModel fragmentViewModel = new ViewModelProvider(this).get(FragmentViewModel.class);
            if (fragmentViewModel.isFragmentListEmpty()) {
                List<Fragment> fragmentList = new ArrayList<>();
                fragmentList.add(new UserDrawerFragment());
                fragmentList.add(new HomeFragment());
                fragmentList.add(new AddUserFragment());
                fragmentViewModel.setFragmentList(fragmentList);
            }
            handleReceivedData(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setWindowColors(R.color.allBackgroundColor2);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        handler = new Handler(Looper.getMainLooper());
        refreshDataRunnable = this::refreshData;
        handler.post(refreshDataRunnable);
    }


    private void handleReceivedData(String jsonString) throws JSONException {
        if (jsonString != null) {
            JSONObject receivedData = new JSONObject(jsonString);
            try {
                User user = decryptUserData(receivedData);
                userViewModel.setUserLive(user);
            } catch (Exception ignored) {
            }
        }
    }

    private User decryptUserData(JSONObject receivedData) throws Exception {
        int userId = Integer.parseInt(EncryptionUtils.decrypt(receivedData.getString("userId")));
        String userDisplayName = EncryptionUtils.decrypt(receivedData.getString("userDisplayName"));
        String userName = EncryptionUtils.decrypt(receivedData.getString("userName"));
        String userDob = EncryptionUtils.decrypt(receivedData.getString("userDob"));
        String userEmail = EncryptionUtils.decrypt(receivedData.getString("userEmail"));
        String userNumber = EncryptionUtils.decrypt(receivedData.getString("userNumber"));
        String userPicture = receivedData.getString("userPicture");
        String userGender = receivedData.getString("userGender");
        boolean userVerified = receivedData.getString("userVerified").equals("Yes");
        String userAccountOpenTime = receivedData.getString("userAccountOpenTime");
        boolean userSecurity = receivedData.getString("userSecurity").equals("Yes");
        String userRole = receivedData.getString("userRole");
        String userActiveStatus = receivedData.getString("userActiveStatus");
        return new User(userId,
                userDisplayName,
                userName,
                userDob,
                userEmail,
                userNumber,
                userPicture,
                userGender,
                userVerified,
                userAccountOpenTime,
                userSecurity,
                userRole,
                userActiveStatus);
    }

    private void refreshData() {
        handleLogin();
        handler.postDelayed(refreshDataRunnable, REFRESH_INTERVAL);
    }

    private void setWindowColors(int colorResId) {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, colorResId));
        window.setNavigationBarColor(ContextCompat.getColor(this, colorResId));
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    private void toastMessage(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void showAllUser() {
        BackgroundWorker backgroundWorker = new BackgroundWorker(this::showAllUserResponse);
        try {
            backgroundWorker.execute("ShowAllUser", EncryptionUtils.encrypt(String.valueOf(userViewModel.getUserLiveData().getValue().getUserId())));
        } catch (Exception e) {
            toastMessage(e.getMessage());
        }
    }

    private void showAllUserResponse(Object output) {
        try {


            JSONArray jsonArr = new JSONArray((String) output);
            userList.clear();
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jsonObj = jsonArr.getJSONObject(i);
                userList.add(0, new User(jsonObj.getInt("id"),
                                               Integer.parseInt(EncryptionUtils.decrypt(jsonObj.getString("userId"))),
                                               EncryptionUtils.decrypt(jsonObj.getString("userDisplayName")),
                                               EncryptionUtils.decrypt(jsonObj.getString("userName")),
                                               jsonObj.getString("userPicture"),
                                               jsonObj.getString("userVerified").equals("Yes"),
                                               jsonObj.getString("userRole"),
                                               jsonObj.getString("userActiveStatus"),
                                               jsonObj.getString("userSecurity").equals("Yes"),
                                  true));
            }
            introsort(userList);
            addUserViewModel.setUserList(userList);
        } catch (Exception e) {
            toastMessage(e.getMessage());
        }
    }

    private static void introsort(ArrayList<User> list) {
        int maxDepth = (int) (2 * Math.log(list.size()) / Math.log(2));
        introsort(list, 0, list.size() - 1, maxDepth);
    }

    private static void introsort(ArrayList<User> list, int low, int high, int maxDepth) {
        if (low < high) {
            if (maxDepth == 0) {
                heapsort(list, low, high);
            } else {
                int pivotIndex = partition(list, low, high);
                introsort(list, low, pivotIndex, maxDepth - 1);
                introsort(list, pivotIndex + 1, high, maxDepth - 1);
            }
        } else {
            insertionSort(list, low, high);
        }
    }

    private static void heapsort(ArrayList<User> list, int low, int high) {
        int n = high - low + 1;
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(list, n, i, low);
        }
        for (int i = n - 1; i >= 0; i--) {
            Collections.swap(list, low, low + i);
            heapify(list, i, 0, low);
        }
    }

    private static void heapify(ArrayList<User> list, int n, int i, int low) {
        int largest = i;
        int leftChild = 2 * i + 1;
        int rightChild = 2 * i + 2;
        if (leftChild < n && list.get(low + leftChild).getUserId() > list.get(low + largest).getUserId()) {
            largest = leftChild;
        }
        if (rightChild < n && list.get(low + rightChild).getUserId() > list.get(low + largest).getUserId()) {
            largest = rightChild;
        }
        if (largest != i) {
            Collections.swap(list, low + i, low + largest);
            heapify(list, n, largest, low);
        }
    }

    private static int partition(ArrayList<User> list, int low, int high) {
        User pivot = list.get(low);
        int i = low - 1;
        int j = high + 1;

        while (true) {
            do {
                i++;
            } while (list.get(i).getUserId() < pivot.getUserId());

            do {
                j--;
            } while (list.get(j).getUserId() > pivot.getUserId());

            if (i >= j) {
                return j;
            }

            Collections.swap(list, i, j);
        }
    }

    private static void insertionSort(ArrayList<User> list, int low, int high) {
        // Implement insertion sort logic here
        for (int i = low + 1; i <= high; i++) {
            User key =  list.get(i);
            int j = i - 1;

            while (j >= low && list.get(j).getUserId() > key.getUserId()) {
                list.set(j + 1, list.get(j));
                j--;
            }

            list.set(j + 1, key);
        }
    }

    private void handleLogin() {
        if (Constant.getDataLogin(MainActivity.this)) {
            final String mainKey = Constant.getDataMainKey(MainActivity.this);
            final String pass = Constant.getDataPass(MainActivity.this);
            BackgroundWorker backgroundWorker = new BackgroundWorker(this::processLoginResponse);
            try {
                if (isValidEmail(mainKey)) {
                    backgroundWorker.execute("Login", EncryptionUtils.encrypt(mainKey), EncryptionUtils.encrypt(""), EncryptionUtils.encrypt(pass));
                } else {
                    backgroundWorker.execute("Login", EncryptionUtils.encrypt(""), EncryptionUtils.encrypt(mainKey), EncryptionUtils.encrypt(pass));
                }
            } catch (Exception ex) {
                startActivity(new Intent(MainActivity.this, AuthActivity.class));
                finish();
                toastMessage(ex.getMessage());
            }
        }
    }
    private void processLoginResponse(Object output) {
        try {
            JSONObject jsonResponse = new JSONObject((String) output);
            if (jsonResponse.getBoolean("success")) {
                User user = decryptUserData(jsonResponse);
                if (userViewModel.getUserLiveData() != null) {
                    if (!user.allEquals(userViewModel.getUserLiveData().getValue())) {
                        userViewModel.setUserLive(user);
                    }
                }

                showAllUser();
            } else {
                toastMessage(jsonResponse.getString("message"));
                Constant.clearData(MainActivity.this);
                startActivity(new Intent(MainActivity.this, AuthActivity.class));
                finish();
            }
        } catch (Exception e) {
//            toastMessage(e.getMessage());
//            startActivity(new Intent(MainActivity.this, AuthActivity.class));
//            finish();
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(refreshDataRunnable);
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_home_host_fragment);
        NavController navController = navHostFragment.getNavController();
        NavDestination currentDestination = navController.getCurrentDestination();
        int currentDestinationId = currentDestination.getId();
        if (currentDestinationId == R.id.mainFragment) {
            Intent setIntent = new Intent("android.intent.action.MAIN");
            setIntent.addCategory("android.intent.category.HOME");
            setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(setIntent);
        } else {
            navController.navigateUp();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(refreshDataRunnable);

    }


    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(refreshDataRunnable, REFRESH_INTERVAL);
    }




}