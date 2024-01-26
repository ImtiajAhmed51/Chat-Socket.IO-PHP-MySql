package com.example.chat.ui.fragment.home;

import static com.example.chat.utils.Constant.isValidEmail;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chat.R;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class AddFriendsFragment extends Fragment implements ClickListener, View.OnClickListener {
    private FragmentAddFriendsBinding binding;
    private UserAdapter userAdapter;
    private UserViewModel userViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddFriendsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Constant.setTopMargin(binding.addFriendsFragmentMargin, DimensionUtils.getStatusBarHeight(requireActivity()));
        Constant.setBottomMargin(binding.cardViewMargin, DimensionUtils.getNavigationBarHeight(requireActivity()));
        binding.addFriendsBackPressed.setOnClickListener(this);
        binding.sentRequestsClick.setOnClickListener(this);
        binding.friendRequestsClick.setOnClickListener(this);
        AddUserViewModel addUserViewModel = new ViewModelProvider(requireActivity()).get(AddUserViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userAdapter = new UserAdapter(requireActivity(),new ArrayList<>(), this, 2);

        binding.addFriendsRecyclerView.setAdapter(userAdapter);
        addUserViewModel.getUserListLiveData().observe(requireActivity(), new Observer<ArrayList<User>>() {
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
    private int findInsertionIndex(ArrayList<User> list, long targetUserId) {
        int low = 0;
        int high = list.size() - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;

            if (list.get(mid).getUserId() == targetUserId) {
                return mid; // Element found, return the index
            } else if (list.get(mid).getUserId() < targetUserId) {
                low = mid + 1; // Search in the right half
            } else {
                high = mid - 1; // Search in the left half
            }
        }

        return low; // Element not found, return the insertion index
    }
    private static int binarySearch(ArrayList<User> list, long target) {
        int low = 0;
        int high = list.size() - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;

            if (list.get(mid).getUserId() == target) {
                return mid; // Target found, return the index
            } else if (list.get(mid).getUserId() < target) {
                low = mid + 1; // Search in the right half
            } else {
                high = mid - 1; // Search in the left half
            }
        }

        return -1; // Target not found
    }


    private void toastMessage(String message) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickItem(User user, int position, int type) {
        performBackgroundWork(String.valueOf(userViewModel.getUserLiveData().getValue().getUserId()),String.valueOf(user.getUserId()));
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

                //int position = userAdapter.getData().indexOf(new User(Integer.parseInt(EncryptionUtils.decrypt(jsonResponse.getString("receiverUserId")))));
               int position=binarySearch(userAdapter.getData(),Integer.parseInt(EncryptionUtils.decrypt(jsonResponse.getString("receiverUserId"))));
                userAdapter.getData().get(position).setButtonEnabled(false);
                userAdapter.notifyDataSetChanged();
            } else {
            }
        } catch (Exception e) {
            toastMessage(e.getMessage());
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == binding.addFriendsBackPressed.getId()) {
            requireActivity().onBackPressed();
        } else if (view.getId()==binding.sentRequestsClick.getId()||view.getId()==binding.friendRequestsClick.getId()) {

            Bundle data = new Bundle();
            String title = null;
            int type=0;
            if(view.getId()==binding.sentRequestsClick.getId()){
                type=1;
                title="Sent Requests";
            } else if (view.getId()==binding.friendRequestsClick.getId()) {
                type=2;
                title="Friend Requests";
            }
            data.putString("title", title);
            data.putInt("type", type);
            Navigation.findNavController(requireView()).navigate(R.id.action_addFriendsFragment_to_requestsFragment, data);
        }
    }
}