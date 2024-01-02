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
import com.example.chat.databinding.FragmentAddUserBinding;
import com.example.chat.inter.ClickListener;
import com.example.chat.model.User;
import com.example.chat.ui.viewmodel.AddUserViewModel;
import com.example.chat.ui.viewmodel.UserViewModel;
import com.example.chat.utils.BackgroundWorker;
import com.example.chat.utils.EncryptionUtils;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
public class AddUserFragment extends Fragment implements ClickListener {
    private FragmentAddUserBinding binding;
    private UserAdapter userAdapter;
    private AddUserViewModel addUserViewModel;
    private UserViewModel userViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddUserBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addUserViewModel = new ViewModelProvider(getActivity()).get(AddUserViewModel.class);
        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        userAdapter = new UserAdapter(requireActivity(), new ArrayList<>(), this, 2);
        binding.userRecyclerView.setAdapter(userAdapter);
        addUserViewModel.getUserListLiveData().observe(getActivity(), new Observer<ArrayList<User>>() {
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
    public void setCardBackgroundColor(int color) {

            binding.groupCardView.setCardBackgroundColor(color);
    }
    private void toastMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
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

                int position=userAdapter.getData().indexOf(new User(EncryptionUtils.decrypt(jsonResponse.getString("receiverUserId"))));
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