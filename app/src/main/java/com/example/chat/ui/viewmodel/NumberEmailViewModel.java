package com.example.chat.ui.viewmodel;

import androidx.lifecycle.ViewModel;

public class NumberEmailViewModel extends ViewModel {
    public static final int MODE_NUMBER = 1;
    public static final int MODE_EMAIL = 2;

    private int check;

    public NumberEmailViewModel() {
        check = MODE_NUMBER;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }
}
