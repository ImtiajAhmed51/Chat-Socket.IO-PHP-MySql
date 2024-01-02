package com.example.chat.adapter;

import android.app.Activity;
import android.util.TypedValue;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> lf = new ArrayList<>();
    private List<String> lt = new ArrayList<>();
    private Activity activity;
    private float leftFragmentWidthPixels;
    private float rightFragmentWidthPixels;

    public ViewPagerAdapter(FragmentManager manager, Activity activity) {
        super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.activity = activity;
    }

    public List<Fragment> getLf() {
        return lf;
    }

    public void setLf(List<Fragment> lf) {
        this.lf = lf;
    }

    public List<String> getLt() {
        return lt;
    }

    public void setLt(List<String> lt) {
        this.lt = lt;
    }

    public void addFragment(Fragment fragment, String title) {
        lf.add(fragment);
        lt.add(title);
        this.leftFragmentWidthPixels = dpToScreenWidthFraction(78);
        this.rightFragmentWidthPixels = dpToScreenWidthFraction(300);
    }

    @Override
    public float getPageWidth(int page) {
        if (page == 0) {
            return leftFragmentWidthPixels;
        } else if (page == 2) {
            return rightFragmentWidthPixels;
        } else {
            return super.getPageWidth(page);
        }
    }

    @Override
    public Fragment getItem(int position) {
        return lf.get(position);
    }

    @Override
    public int getCount() {
        return lf.size();
    }

    public Fragment getFragment(int position) {
        return lf.get(position);
    }

    private float dpToScreenWidthFraction(float dpValue) {
        float pixels = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dpValue,
                activity.getResources().getDisplayMetrics());
        return pixels / (float) activity.getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return lt.get(position);
    }
}
