package com.example.chat.ui.fragment;
import android.animation.ArgbEvaluator;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.chat.R;
import com.example.chat.adapter.ViewPagerAdapter;
import com.example.chat.databinding.FragmentMainBinding;
import com.example.chat.ui.fragment.home.AddUserFragment;
import com.example.chat.ui.fragment.home.HomeFragment;
import com.example.chat.ui.fragment.home.UserDrawerFragment;
import com.example.chat.ui.viewmodel.FragmentViewModel;
public class MainFragment extends Fragment {
    private FragmentMainBinding binding;
    private ViewPagerAdapter viewAdapter;
    private FragmentViewModel fragmentViewModel;
    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    public MainFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentViewModel = new ViewModelProvider(getActivity()).get(FragmentViewModel.class);
        viewAdapter = new ViewPagerAdapter(getChildFragmentManager(), getActivity());
        setUpViewPager();
    }

    private void setUpViewPager() {
        viewAdapter.addFragment(fragmentViewModel.getFragmentLiveData().getValue().get(0), "UserDrawer");
        viewAdapter.addFragment(fragmentViewModel.getFragmentLiveData().getValue().get(1),"HomeFragment");
        viewAdapter.addFragment(fragmentViewModel.getFragmentLiveData().getValue().get(2),"AddUserFragment");
        setTopBottomMargin(binding.viewPager,getStatusBarHeight(),getNavigationBarHeight());
        binding.viewPager.setAdapter(viewAdapter);
        binding.viewPager.setCurrentItem(1);
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                HomeFragment homeFragment = (HomeFragment) viewAdapter.getFragment(1);
                UserDrawerFragment userDrawerFragment = (UserDrawerFragment) viewAdapter.getFragment(0);
                AddUserFragment addUserFragment = (AddUserFragment) viewAdapter.getFragment(2);
                if (position == 0) {
                    int color = (int) argbEvaluator.evaluate(positionOffset, getColor(0), getColor(1));
                    homeFragment.setCardBackgroundColor(color);
                    userDrawerFragment.setCardBackgroundColor(color);
                    addUserFragment.setCardBackgroundColor(color);
                } else if (position == 1) {
                    int color = (int) argbEvaluator.evaluate(positionOffset, getColor(1), getColor(2));
                    homeFragment.setCardBackgroundColor(color);
                    userDrawerFragment.setCardBackgroundColor(color);
                    addUserFragment.setCardBackgroundColor(color);
                } else if (position == 2) {
                    int color = (int) argbEvaluator.evaluate(positionOffset, getColor(0), getColor(1));
                    homeFragment.setCardBackgroundColor(color);
                    userDrawerFragment.setCardBackgroundColor(color);
                    addUserFragment.setCardBackgroundColor(color);
                }
            }
            @Override
            public void onPageSelected(int position) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
    private void setTopBottomMargin(ViewGroup viewGroup, int topMargin,int bottomMargin) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) viewGroup.getLayoutParams();
        params.topMargin = topMargin;
        params.bottomMargin = bottomMargin;
        viewGroup.setLayoutParams(params);
    }
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    private int getNavigationBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    private int getColor(int position) {
        switch (position) {
            case 0:
                return getResources().getColor(R.color.allBackgroundColor5);
            case 1:
                return getResources().getColor(R.color.allBackgroundColor2);
            case 2:
                return getResources().getColor(R.color.allBackgroundColor3);
            default:
                return getResources().getColor(R.color.allBackgroundColor3);
        }
    }
}