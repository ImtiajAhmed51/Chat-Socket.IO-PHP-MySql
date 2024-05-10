package com.example.chat.ui.fragment.home;
import static com.example.chat.utils.Constant.binarySearch;
import static com.example.chat.utils.Constant.findInsertionIndex;
import static com.example.chat.utils.Constant.introSort;
import static com.example.chat.utils.Constant.userUpdate;

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
import com.example.chat.databinding.FragmentAddFriendsBinding;
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

public class AddFriendsFragment extends Fragment implements ClickListener, View.OnClickListener {
    private FragmentAddFriendsBinding binding;
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
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        ownViewModel = new ViewModelProvider(requireActivity()).get(OwnViewModel.class);
        userAdapter = new UserAdapter(requireActivity(),new ArrayList<>(), this, 2);

        handler = new Handler(Looper.getMainLooper());
        refreshDataRunnable = this::refreshData;
        handler.post(refreshDataRunnable);
    }
    private void refreshData() {

        showAllUser();
        handler.postDelayed(refreshDataRunnable, REFRESH_INTERVAL);
    }

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

        binding.addFriendsRecyclerView.setAdapter(userAdapter);
        userViewModel.getUserListLiveData().observe(requireActivity(), new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> userList) {
                if (getActivity() != null) {
                    userUpdate(userList,userAdapter);
                }
            }
        });
    }

    private void showAllUser() {
        BackgroundWorker backgroundWorker = new BackgroundWorker(this::showAllUserResponse);
        try {
            backgroundWorker.execute("ShowAllUser", EncryptionUtils.encrypt(String.valueOf(ownViewModel.getUserLiveData().getValue().getUserId())));
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
                        jsonObj.getString("userSecurity").equals("Yes"),true));
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
    public void onClickItem(User user, int position, int type,int buttonType) {
        if(type==2&&buttonType==0){
            performBackgroundWork(String.valueOf(ownViewModel.getUserLiveData().getValue().getUserId()),String.valueOf(user.getUserId()));
        }
    }

    @Override
    public void onClickGalleryImage(int position, int type) {

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
                userAdapter.getData().get(position).setRequestSuccess(true);
                userAdapter.notifyItemChanged(position);
            } else {
                int position=binarySearch(userAdapter.getData(),Integer.parseInt(EncryptionUtils.decrypt(jsonResponse.getString("receiverUserId"))));
                userAdapter.getData().get(position).setButtonEnabled(true);
                userAdapter.getData().get(position).setRequestSuccess(false);
                userAdapter.notifyItemChanged(position);
            }
        } catch (Exception e) {
            toastMessage(e.getMessage());
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

    @Override
    public void onClick(View view) {
        if (view.getId() == binding.addFriendsBackPressed.getId()) {
            requireActivity().onBackPressed();
        } else if (view.getId()==binding.sentRequestsClick.getId()||view.getId()==binding.friendRequestsClick.getId()) {
            Bundle data = new Bundle();
            String title = null;
            int type=0;
            if(view.getId()==binding.sentRequestsClick.getId()){
                type=3;
                title="Sent Requests";
            } else if (view.getId()==binding.friendRequestsClick.getId()) {
                type=4;
                title="Friend Requests";
            }
            data.putString("title", title);
            data.putInt("type", type);
            Navigation.findNavController(requireView()).navigate(R.id.action_addFriendsFragment_to_requestsFragment, data);
        }
    }
}