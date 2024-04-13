package com.example.chat.ui.fragment.home;

import static com.example.chat.utils.Constant.binarySearch;
import static com.example.chat.utils.Constant.findInsertionIndex;
import static com.example.chat.utils.Constant.getResource;
import static com.example.chat.utils.Constant.userUpdate;

import android.content.Intent;
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

import com.bumptech.glide.Glide;
import com.example.chat.R;
import com.example.chat.adapter.UserAdapter;
import com.example.chat.databinding.FragmentUserDrawerBinding;
import com.example.chat.inter.ClickListener;
import com.example.chat.model.User;
import com.example.chat.ui.activity.ChatUserActivity;
import com.example.chat.ui.viewmodel.UserViewModel;
import com.example.chat.ui.viewmodel.OwnViewModel;
import com.example.chat.utils.Constant;
import com.example.chat.utils.DimensionUtils;

import java.util.ArrayList;

public class UserDrawerFragment extends Fragment implements View.OnClickListener, ClickListener {
    private FragmentUserDrawerBinding binding;
    private UserAdapter userAdapter;
    private OwnViewModel ownViewModel;
    private UserViewModel userViewModel;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUserDrawerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ownViewModel = new ViewModelProvider(requireActivity()).get(OwnViewModel.class);
        Constant.setTopMargin(binding.userDrawerLinearLayout, DimensionUtils.getStatusBarHeight(requireActivity()));
        binding.userProfileClick.setOnClickListener(this);


        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userAdapter = new UserAdapter(requireActivity(),new ArrayList<>(), this, 1);


        ownViewModel.getUserLiveData().observe(requireActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                Glide.with(requireActivity()).load(getResource(user.getUserPicture())).into(binding.drawerUserImage);
                binding.userProfileActiveStatus.setImageResource(Constant.getUserActiveStatus(user.getUserActiveStatus()));

            }
        });
        binding.drawerRecyclerView.setAdapter(userAdapter);
        userViewModel.getUserListLiveData().observe(requireActivity(), new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> userList) {
                if (getActivity() != null) {
                    userUpdate(userList,userAdapter);
                }
            }
        });
    }




    @Override
    public void onClick(View view) {
        if (view.getId() == binding.userProfileClick.getId()) {

            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_mainFragment_to_profileFragment);
        }
    }

    @Override
    public void onClickItem(User user, int position, int type, int buttonType) {
        if(type==1){
            startActivity(new Intent(requireActivity(), ChatUserActivity.class));
            //Navigation.findNavController(binding.getRoot()).navigate(R.id.action_mainFragment_to_chatUserFragment);
        }

    }
}