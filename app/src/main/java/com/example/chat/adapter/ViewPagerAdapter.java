package com.example.chat.adapter;

import android.app.Activity;
import android.content.res.Configuration;
import android.util.TypedValue;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> fragmentTitle = new ArrayList<>();
    private Activity activity;
    private float leftFragmentWidthPixels;

    public ViewPagerAdapter(FragmentManager manager, Activity activity) {
        super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.activity = activity;
    }

    public List<Fragment> getLf() {
        return fragmentList;
    }

    public void setLf(List<Fragment> lf) {
        this.fragmentList = lf;
    }

    public List<String> getLt() {
        return fragmentTitle;
    }

    public void setLt(List<String> lt) {
        this.fragmentTitle = lt;
    }

    public void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        fragmentTitle.add(title);
        this.leftFragmentWidthPixels = dpToScreenWidthFraction();
    }

    @Override
    public float getPageWidth(int page) {
        if (page == 0) {
            return leftFragmentWidthPixels;
        } else {
            return super.getPageWidth(page);
        }
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    private float dpToScreenWidthFraction() {
        // Get the current orientation
        int orientation = activity.getResources().getConfiguration().orientation;
        float dpValue = 74; // Keep 74 as the base dp value for portrait

        // Use different scaling factor for landscape mode
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            float aspectRatio = (float) activity.getResources().getDisplayMetrics().widthPixels /
                    (float) activity.getResources().getDisplayMetrics().heightPixels;
            dpValue = dpValue * aspectRatio; // Scale dp based on the aspect ratio in landscape
        }

        float pixels = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dpValue,
                activity.getResources().getDisplayMetrics());

        return pixels / (float) activity.getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitle.get(position);
    }
}