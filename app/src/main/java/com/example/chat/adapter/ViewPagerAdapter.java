package com.example.chat.adapter;

import android.app.Activity;
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
        float pixels = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                (float) 74,
                activity.getResources().getDisplayMetrics());
        return pixels / (float) activity.getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitle.get(position);
    }
}
