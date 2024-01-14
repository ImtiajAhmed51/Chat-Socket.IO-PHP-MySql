package com.example.chat.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class FragmentViewModel extends AndroidViewModel {
    private final MutableLiveData<List<Fragment>> fragmentsLiveData;

    public FragmentViewModel(@NonNull Application application) {
        super(application);
        fragmentsLiveData = new MutableLiveData<>();
    }

    public void setFragmentList(List<Fragment> userList) {
        fragmentsLiveData.setValue(userList);
    }

    public LiveData<List<Fragment>> getFragmentLiveData() {
        return fragmentsLiveData;
    }

    public boolean isFragmentListEmpty() {
        List<Fragment> fragments = fragmentsLiveData.getValue();
        return fragments == null || fragments.isEmpty();
    }
}
