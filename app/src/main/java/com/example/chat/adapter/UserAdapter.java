package com.example.chat.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat.databinding.AddUserBinding;
import com.example.chat.databinding.DrawerUserBinding;
import com.example.chat.inter.ClickListener;
import com.example.chat.model.User;
import com.example.chat.utils.Constant;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Activity activity;
    private final ArrayList<User> users;
    private final ClickListener itemClick;
    private final int type;

    public UserAdapter(Activity activity, ArrayList<User> users, ClickListener itemClick, int type) {
        this.activity = activity;
        this.users = users;
        this.itemClick = itemClick;
        this.type = type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (type) {
            case 1:
                return new UserViewHolder(DrawerUserBinding.inflate(inflater, parent, false));
            case 2:
                return new AddUserViewHolder(AddUserBinding.inflate(inflater, parent, false));
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    public ArrayList<User> getData() {
        return users;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        User currentItem = users.get(position);
        switch (type) {
            case 1:
                bindUserViewHolder((UserViewHolder) holder, currentItem);
                break;
            case 2:
                bindAddUserViewHolder((AddUserViewHolder) holder, currentItem);
                break;
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    private void bindUserViewHolder(UserViewHolder holder, User currentItem) {
        // Add logic for binding UserViewHolder if needed
    }

    private void bindAddUserViewHolder(AddUserViewHolder holder, User currentItem) {
        AddUserBinding addUserBinding = holder.addUserBinding;
        addUserBinding.addRequestClick.setVisibility(currentItem.isButtonEnabled() ? View.VISIBLE : View.INVISIBLE);
        addUserBinding.addRequestClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClick.onClickItem(currentItem, holder.getAdapterPosition(), type);
            }
        });
        Glide.with(activity).load(Constant.getResource(currentItem.getUserPicture())).into(addUserBinding.addUserPicture);
        addUserBinding.addUserDisplayName.setText(currentItem.getUserDisplayName());
        addUserBinding.allUserVerifiedId.setVisibility(currentItem.isUserVerified() ? View.VISIBLE : View.GONE);
        addUserBinding.userRole.setVisibility(currentItem.getUserRole().equals("ADMIN") ? View.VISIBLE : View.INVISIBLE);

        if(!currentItem.isUserSecurity()){
            addUserBinding.addUserNameAndUserId.setText(currentItem.getUserName() + "#"+String.valueOf(currentItem.getUserId()));
            addUserBinding.activeStatusLayout.setVisibility(View.VISIBLE);
            addUserBinding.addFriendsUserActiveStatus.setImageResource(Constant.getUserActiveStatusResource(currentItem.getUserActiveStatus()));
        }else{
            addUserBinding.addUserNameAndUserId.setText("");
            addUserBinding.activeStatusLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public final DrawerUserBinding userBinding;

        public UserViewHolder(DrawerUserBinding userBinding) {
            super(userBinding.getRoot());
            this.userBinding = userBinding;
        }
    }

    public static class AddUserViewHolder extends RecyclerView.ViewHolder {
        public final AddUserBinding addUserBinding;

        public AddUserViewHolder(AddUserBinding addUserBinding) {
            super(addUserBinding.getRoot());
            this.addUserBinding = addUserBinding;
        }
    }
}