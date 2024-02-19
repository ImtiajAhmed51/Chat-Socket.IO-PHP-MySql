package com.example.chat.ui.fragment.home;
import static com.example.chat.utils.Constant.binarySearch;
import static com.example.chat.utils.Constant.findInsertionIndex;
import static com.example.chat.utils.Constant.introSort;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chat.adapter.UserAdapter;
import com.example.chat.databinding.FragmentRequestsBinding;
import com.example.chat.inter.ClickListener;
import com.example.chat.model.User;
import com.example.chat.ui.viewmodel.UserViewModel;
import com.example.chat.ui.viewmodel.OwnViewModel;
import com.example.chat.utils.BackgroundWorker;
import com.example.chat.utils.Constant;
import com.example.chat.utils.DimensionUtils;
import com.example.chat.utils.EncryptionUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class RequestsFragment extends Fragment implements View.OnClickListener, ClickListener {
    private FragmentRequestsBinding binding;
    private String title;
    private int intType;
    private UserAdapter userAdapter;
    private ArrayList<User> userList=new ArrayList<>();
    private OwnViewModel ownViewModel;
    private UserViewModel userViewModel;
    private static final long REFRESH_INTERVAL = 2000;
    private Handler handler;
    private Runnable refreshDataRunnable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeArguments();
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        ownViewModel = new ViewModelProvider(requireActivity()).get(OwnViewModel.class);
        userAdapter = new UserAdapter(requireActivity(),new ArrayList<>(), this, intType);
        handler = new Handler(Looper.getMainLooper());
        refreshDataRunnable = this::refreshData;
        handler.post(refreshDataRunnable);
    }


    private void refreshData() {
        if(intType==3){
            showAllUser("PendingRequestShowAllUser");
            handler.postDelayed(refreshDataRunnable, REFRESH_INTERVAL);
        } else if (intType==4) {
            showAllUser("SentRequestShowAllUser");
            handler.postDelayed(refreshDataRunnable, REFRESH_INTERVAL);
        }
    }
    private void showAllUser(String type) {
        BackgroundWorker backgroundWorker = new BackgroundWorker(this::showAllUserResponse);
        try {
            backgroundWorker.execute(type, EncryptionUtils.encrypt(String.valueOf(ownViewModel.getUserLiveData().getValue().getUserId())));
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
                        jsonObj.getString("requestTime"),
                        true));
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRequestsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Constant.setTopMargin(binding.requestsFragmentMargin, DimensionUtils.getStatusBarHeight(requireActivity()));
        Constant.setBottomMargin(binding.requestsCardViewMargin, DimensionUtils.getNavigationBarHeight(requireActivity()));


        binding.requestsTitle.setText(title);
        binding.requestsBackPressed.setOnClickListener(this);


        binding.addFriendsRecyclerView.setAdapter(userAdapter);
        userViewModel.getUserListLiveData().observe(requireActivity(), new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> userList) {
                if (getActivity() != null) {
                    ArrayList<User> existingData = userAdapter.getData();
                    ArrayList<User> newUserIds = new ArrayList<>();

                    int existingIndex, insertionIndex;

                    for (User user : userList) {
                        existingIndex = binarySearch(existingData, user.getUserId());
                        newUserIds.add(user);

                        if (existingIndex == -1) {
                            // Element not found, insert at the correct position to maintain sorted order
                            insertionIndex = findInsertionIndex(existingData, user.getUserId());
                            existingData.add(insertionIndex, user);
                            userAdapter.notifyItemInserted(insertionIndex);
                        } else {
                            User existingUser = existingData.get(existingIndex);
                            if (!existingUser.addFriendsUserEqual(user)) {
                                // Element found, update if necessary
                                existingData.set(existingIndex, user);
                                userAdapter.notifyItemChanged(existingIndex);
                            }
                        }
                    }

                    for (int i = existingData.size() - 1; i >= 0; i--) {
                        User existingUser = existingData.get(i);
                        int position=binarySearch(newUserIds,existingUser.getUserId());
                        if (position==-1) {
                            existingData.remove(i);
                            userAdapter.notifyItemRemoved(i);
                        }
                    }
                }
            }
        });


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
    private void initializeArguments() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            title = arguments.getString("title");
            intType = arguments.getInt("type");

        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==binding.requestsBackPressed.getId()){
            requireActivity().onBackPressed();
        }
    }


    public void onClickItem(User user, int position, int type,int buttonType) {
            performBackgroundWork(String.valueOf(user.getId()),type,buttonType);
    }

    private void performBackgroundWork(String friendUserId,int type,int buttonType) {
        try {
            BackgroundWorker backgroundWorker = new BackgroundWorker(this::pendingRequestCancelUserResponse);
            if(type==3&&buttonType==0){
                backgroundWorker.execute("PendingRequestCancelUser", friendUserId);
            } else if (type==4&&buttonType==0) {
                backgroundWorker.execute("SentRequestCancelUser", friendUserId);
            } else if (type==4&&buttonType==1) {
                backgroundWorker.execute("SentRequestAcceptUser", friendUserId);
            }

        } catch (Exception e) {
            toastMessage(e.getMessage());
        }
    }

    private void pendingRequestCancelUserResponse(Object output) {
        try {
            JSONObject jsonResponse = new JSONObject((String) output);
            if (jsonResponse.getBoolean("success")) {

                int position=binarySearch(userAdapter.getData(),Integer.parseInt(EncryptionUtils.decrypt(jsonResponse.getString("userId"))));
                userAdapter.getData().get(position).setButtonEnabled(false);
                userAdapter.notifyDataSetChanged();
            } else {
            }
        } catch (Exception e) {
            toastMessage(e.getMessage());
        }
    }
}