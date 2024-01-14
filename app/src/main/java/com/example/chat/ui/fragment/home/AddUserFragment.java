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
import android.widget.AdapterView;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.example.chat.R;
import com.example.chat.adapter.CustomAddUserAdapter;
import com.example.chat.adapter.UserAdapter;
import com.example.chat.databinding.FragmentAddUserBinding;
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

public class AddUserFragment extends Fragment implements ClickListener {
    private FragmentAddUserBinding binding;
    private UserAdapter userAdapter;
    private UserViewModel userViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddUserBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String[] addUserText={"Add Friends","Pending Friends","Send Friends"};
        int addUserImages[] = {R.drawable.add_users,R.drawable.add_accept_user, R.drawable.add_request_user};




        binding.addUserSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(MainActivity.this, "You Select Position: "+position+" "+fruits[position], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        CustomAddUserAdapter customAdapter=new CustomAddUserAdapter(requireActivity(),addUserImages,addUserText);
        binding.addUserSpinner.setAdapter( customAdapter);

        AddUserViewModel addUserViewModel = new ViewModelProvider(requireActivity()).get(AddUserViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        Constant.setTopMargin(binding.addUserRelativeLayout, DimensionUtils.getStatusBarHeight(requireActivity()));
        userAdapter = new UserAdapter(requireActivity(), new ArrayList<>(), this, 2);
        binding.userRecyclerView.setAdapter(userAdapter);
        addUserViewModel.getUserListLiveData().observe(requireActivity(), new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> userList) {
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
                        if (!existingUser.customEqual(user)) {
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
        addRequestUser(userViewModel.getUserLiveData().getValue().getUserId(), user.getUserId());
    }


    private void addRequestUser(String senderUserId, String receiverUserId) {

        BackgroundWorker backgroundWorker = new BackgroundWorker(this::AddRequestUserResponse);
        try {

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
}