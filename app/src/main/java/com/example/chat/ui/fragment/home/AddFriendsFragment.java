package com.example.chat.ui.fragment.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.chat.adapter.UserAdapter;
import com.example.chat.databinding.FragmentAddFriendsBinding;
import com.example.chat.inter.ClickListener;
import com.example.chat.model.User;
import com.example.chat.ui.viewmodel.AddUserViewModel;
import com.example.chat.ui.viewmodel.UserViewModel;
import com.example.chat.utils.BackgroundWorker;
import com.example.chat.utils.Constant;
import com.example.chat.utils.DimensionUtils;
import com.example.chat.utils.EncryptionUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class AddFriendsFragment extends Fragment implements ClickListener,View.OnClickListener {
    private FragmentAddFriendsBinding binding;
    private UserAdapter userAdapter;
    private UserViewModel userViewModel;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        binding = FragmentAddFriendsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Constant.setTopMargin(binding.addFriendsFragmentMargin, DimensionUtils.getStatusBarHeight(requireActivity()));
        Constant.setBottomMargin(binding.constraintLayoutMargin,DimensionUtils.getNavigationBarHeight(requireActivity()));
        binding.addFriendsBackPressed.setOnClickListener(this);
        AddUserViewModel addUserViewModel = new ViewModelProvider(requireActivity()).get(AddUserViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userAdapter = new UserAdapter(requireActivity(),new ArrayList<>(), this, 2);
        binding.addFriendsRecyclerView.setAdapter(userAdapter);

        addUserViewModel.getUserListLiveData().observe(requireActivity(), new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> userList) {
                if (getActivity() != null) {
                    ArrayList<User> existingData = userAdapter.getData();
                    HashSet<String> newUserIds = new HashSet<>();
                    for (User user : userList) {
                        int existingIndex = findUserIndex(existingData, user.getUserId());
                        newUserIds.add(user.getUserId());
                        if (existingIndex == -1) {
                            existingData.add(user);
                            userAdapter.notifyItemInserted(existingData.size() - 1);
                        } else {
                            User existingUser = userAdapter.getData().get(existingIndex);
                            if (!existingUser.addFriendsUserEqual(user)) {
                                userAdapter.getData().set(existingIndex, user);
                                userAdapter.notifyItemChanged(existingIndex);
                            }
                        }
                    }
                    for (int i = existingData.size() - 1; i >= 0; i--) {
                        User existingUser = existingData.get(i);
                        if (!newUserIds.contains(existingUser.getUserId())) {
                            existingData.remove(i);
                            userAdapter.notifyItemRemoved(i);
                        }
                    }
                }

            }
        });
    }
    private int findUserIndex(List<User> userList, String userId) {
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUserId().equals(userId)) {
                return i;
            }
        }
        return -1;
    }

    private void toastMessage(String message) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickItem(User user, int position, int type) {
        performBackgroundWork(Objects.requireNonNull(userViewModel.getUserLiveData().getValue()).getUserId(), user.getUserId());
    }


    private void performBackgroundWork(String senderUserId, String receiverUserId) {
        try {
            BackgroundWorker backgroundWorker = new BackgroundWorker(this::AddRequestUserResponse);
            backgroundWorker.execute("AddRequestUser", EncryptionUtils.encrypt(senderUserId), EncryptionUtils.encrypt(receiverUserId));
        } catch (Exception e) {
            toastMessage(e.getMessage());
        }
    }

    private void AddRequestUserResponse(Object output) {
        try {
            JSONObject jsonResponse = new JSONObject((String) output);
            if (jsonResponse.getBoolean("success")) {

                int position = userAdapter.getData().indexOf(new User(EncryptionUtils.decrypt(jsonResponse.getString("receiverUserId"))));
                userAdapter.getData().get(position).setButtonEnabled(false);
                //userAdapter.notifyItemChanged(position);
                userAdapter.notifyDataSetChanged();
                //toastMessage("Touch Working");
            } else {
            }
        } catch (Exception e) {
            toastMessage(e.getMessage());
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==binding.addFriendsBackPressed.getId()){
            requireActivity().onBackPressed();
        }
    }
}