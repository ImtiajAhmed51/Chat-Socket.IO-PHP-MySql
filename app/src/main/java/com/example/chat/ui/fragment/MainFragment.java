package com.example.chat.ui.fragment;
import android.animation.ArgbEvaluator;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chat.R;
import com.example.chat.adapter.ViewPagerAdapter;
import com.example.chat.databinding.FragmentMainBinding;
import com.example.chat.ui.viewmodel.FragmentViewModel;
import com.example.chat.utils.Constant;
import com.example.chat.utils.DimensionUtils;
import com.google.android.material.tabs.TabLayout;

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
        fragmentViewModel = new ViewModelProvider(requireActivity()).get(FragmentViewModel.class);
        viewAdapter = new ViewPagerAdapter(getChildFragmentManager(), getActivity());

        setUpViewPager();
    }

    private void setUpViewPager() {
        viewAdapter.addFragment(fragmentViewModel.getFragmentLiveData().getValue().get(0), "UserDrawer");
        viewAdapter.addFragment(fragmentViewModel.getFragmentLiveData().getValue().get(1),"HomeFragment");
        viewAdapter.addFragment(fragmentViewModel.getFragmentLiveData().getValue().get(2),"AddUserFragment");
        setBottomMargin(binding.tabs, DimensionUtils.getNavigationBarHeight(requireActivity()));

        binding.viewPager.setOffscreenPageLimit(1);
        binding.viewPager.setAdapter(viewAdapter);
        binding.tabs.setupWithViewPager(binding.viewPager);
        binding.viewPager.setCurrentItem(1);
        for (int i = 0; i < binding.tabs.getTabCount(); i++) {
            TabLayout.Tab tab = binding.tabs.getTabAt(i);

            if (tab != null) {
                tab.setText(Constant.TAB_TEXTS[i]);
                tab.setIcon(Constant.TAB_ICONS[i]);
            }
        }
    }
    private void setBottomMargin(ViewGroup viewGroup,int bottomMargin) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) viewGroup.getLayoutParams();
        params.bottomMargin = bottomMargin;
        viewGroup.setLayoutParams(params);
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