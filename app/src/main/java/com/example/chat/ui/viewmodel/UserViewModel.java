package com.example.chat.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.chat.model.User;

import java.util.ArrayList;

public class UserViewModel extends AndroidViewModel {
    private final MutableLiveData<ArrayList<User>> userListLiveData;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userListLiveData = new MutableLiveData<>();
    }

    public void setUserList(ArrayList<User> userList) {
        userListLiveData.setValue(userList);
    }

    public LiveData<ArrayList<User>> getUserListLiveData() {
        return userListLiveData;
    }


}
