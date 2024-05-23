package com.example.chat.ui.fragment.home;
import static com.example.chat.utils.Constant.userUpdate;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.SimpleItemAnimator;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.chat.R;
import com.example.chat.adapter.UserAdapter;
import com.example.chat.databinding.FragmentFriendsBinding;
import com.example.chat.inter.ClickListener;
import com.example.chat.model.User;
import com.example.chat.ui.activity.ChatUserActivity;
import com.example.chat.ui.viewmodel.UserViewModel;
import com.example.chat.utils.Constant;
import com.example.chat.utils.DimensionUtils;
import java.util.ArrayList;

public class FriendsFragment extends Fragment implements View.OnClickListener, ClickListener {
    private FragmentFriendsBinding binding;
    private UserAdapter userAdapter;
    private UserViewModel userViewModel;
    private Handler handler = new Handler();
    private String[] strings = {"User Id", "User Name", "Email", "Number"};
    private int stringIndex = 0;
    private int charIndex = 0;
    private boolean incrementing = true;

    private  boolean checker=false;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFriendsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userAdapter = new UserAdapter(requireActivity(), new ArrayList<>(), this, 5);


        if(!checker){
            animateText();
            checker=true;
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Constant.setTopMargin(binding.FriendsFragmentMargin, DimensionUtils.getStatusBarHeight(requireActivity()));
        Constant.setBottomMargin(binding.cardViewMargin, DimensionUtils.getNavigationBarHeight(requireActivity()));
        binding.friendsBackPressed.setOnClickListener(this);
        binding.addFriendsPressed.setOnClickListener(this);


        new Handler().postDelayed(new Runnable() {
            public void run() {

                binding.friendsRecyclerView.setAdapter(userAdapter);
            }
        }, 250);


        ((SimpleItemAnimator) binding.friendsRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        userViewModel.getUserListLiveData().observe(requireActivity(), new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> userList) {
                if (getActivity() != null) {
                    userUpdate(userList, userAdapter, 2);
                }
            }
        });



    }
    private void animateText() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (incrementing) {
                    if (charIndex <= strings[stringIndex].length()) {
                        binding.searchText.setHint(strings[stringIndex].substring(0, charIndex));
                        charIndex++;
                    } else {
                        incrementing = false;
                        charIndex--;
                    }
                } else {
                    if (charIndex >= 0) {
                        binding.searchText.setHint(strings[stringIndex].substring(0, charIndex));
                        charIndex--;
                    } else {
                        incrementing = true;
                        charIndex = 0;
                        stringIndex = (stringIndex + 1) % strings.length;
                    }
                }

                handler.postDelayed(this, 100); // 100 milliseconds delay
            }
        }, 100);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == binding.friendsBackPressed.getId()) {
            requireActivity().onBackPressed();
        } else if (view.getId() == binding.addFriendsPressed.getId()) {
            Navigation.findNavController(requireView()).navigate(R.id.action_friendsFragment_to_addFriendsFragment);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onResume() {
        super.onResume();
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