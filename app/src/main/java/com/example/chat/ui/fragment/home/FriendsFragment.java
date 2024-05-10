package com.example.chat.ui.fragment.home;

import static com.example.chat.utils.Constant.binarySearch;
import static com.example.chat.utils.Constant.findInsertionIndex;
import static com.example.chat.utils.Constant.friendUpdate;
import static com.example.chat.utils.Constant.introSort;
import static com.example.chat.utils.Constant.userUpdate;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chat.R;
import com.example.chat.adapter.UserAdapter;
import com.example.chat.databinding.FragmentFriendsBinding;
import com.example.chat.inter.ClickListener;
import com.example.chat.model.User;
import com.example.chat.ui.activity.ChatUserActivity;
import com.example.chat.ui.viewmodel.UserViewModel;
import com.example.chat.ui.viewmodel.OwnViewModel;
import com.example.chat.utils.BackgroundWorker;
import com.example.chat.utils.Constant;
import com.example.chat.utils.DimensionUtils;
import com.example.chat.utils.EncryptionUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FriendsFragment extends Fragment implements View.OnClickListener, ClickListener {
    private FragmentFriendsBinding binding;
    private UserAdapter userAdapter;
    private final ArrayList<User> userList = new ArrayList<>();
    private OwnViewModel ownViewModel;
    private UserViewModel userViewModel;
    private static final long REFRESH_INTERVAL = 2000;
    private Handler handler;
    private Runnable refreshDataRunnable;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFriendsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        ownViewModel = new ViewModelProvider(requireActivity()).get(OwnViewModel.class);
        userAdapter = new UserAdapter(requireActivity(), new ArrayList<>(), this, 5);
        handler = new Handler(Looper.getMainLooper());
        refreshDataRunnable = this::refreshData;
        handler.post(refreshDataRunnable);
    }

    private void refreshData() {

        showAllUser();
        handler.postDelayed(refreshDataRunnable, REFRESH_INTERVAL);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Constant.setTopMargin(binding.FriendsFragmentMargin, DimensionUtils.getStatusBarHeight(requireActivity()));
        Constant.setBottomMargin(binding.cardViewMargin, DimensionUtils.getNavigationBarHeight(requireActivity()));
        binding.friendsBackPressed.setOnClickListener(this);
        binding.addFriendsPressed.setOnClickListener(this);


        binding.friendsRecyclerView.setAdapter(userAdapter);
        userViewModel.getUserListLiveData().observe(requireActivity(), new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> userList) {
                if (getActivity() != null) {
                    friendUpdate(userList, userAdapter);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == binding.friendsBackPressed.getId()) {
            requireActivity().onBackPressed();
        } else if (view.getId() == binding.addFriendsPressed.getId()) {

            Navigation.findNavController(requireView()).navigate(R.id.action_friendsFragment_to_addFriendsFragment);
        }
    }

    private void showAllUser() {
        BackgroundWorker backgroundWorker = new BackgroundWorker(this::friendsShowAllUserResponse);
        try {
            backgroundWorker.execute("FriendsShowAllUser", EncryptionUtils.encrypt(String.valueOf(ownViewModel.getUserLiveData().getValue().getUserId())));
        } catch (Exception e) {
            toastMessage(e.getMessage());
        }
    }

    private void friendsShowAllUserResponse(Object output) {
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
                        jsonObj.getString("requestTime"),true));
            }
            introSort(userList);
            userViewModel.setUserList(userList);
        } catch (Exception e) {
            toastMessage(e.getMessage());
        }
    }

    private void toastMessage(String message) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClickItem(User user, int position, int type, int buttonType) {
        if (type == 5) {
            Intent intent = new Intent(requireActivity(), ChatUserActivity.class);
            Bundle b = new Bundle();
            b.putLong("id", user.getId());
            b.putLong("userId", user.getUserId());
            b.putString("userDisplayName", user.getUserDisplayName());
            b.putString("userName", user.getUserName());
            b.putString("userPicture", user.getUserPicture());
            b.putBoolean("userVerified", user.isUserVerified());
            b.putString("userRole", user.getUserRole());
            b.putString("userActiveStatus", user.getUserActiveStatus());
            b.putBoolean("userSecurity", user.isUserSecurity());
            intent.putExtras(b);
            startActivity(intent);
        }


    }

    @Override
    public void onClickGalleryImage(int position, int type) {

    }
}