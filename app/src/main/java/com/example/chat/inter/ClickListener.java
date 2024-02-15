package com.example.chat.inter;

import com.example.chat.model.User;
public interface ClickListener {
    void onClickItem(User user, int position, int type,int buttonType);
}
