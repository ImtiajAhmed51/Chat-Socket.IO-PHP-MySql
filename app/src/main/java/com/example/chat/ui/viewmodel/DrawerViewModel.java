package com.example.chat.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.chat.model.User;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

public class DrawerViewModel extends AndroidViewModel {
    private MutableLiveData<ArrayList<User>> userListLiveData;

    public DrawerViewModel(@NonNull Application application) {
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
