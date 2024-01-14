package com.example.chat.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.chat.model.User;

public class UserViewModel extends AndroidViewModel {
    private final MutableLiveData<User> userLiveData;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userLiveData = new MutableLiveData<>();
    }

    public void setUserLive(User user) {
        userLiveData.setValue(user);
    }

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

}
