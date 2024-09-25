package com.example.chat.adapter;

import static com.example.chat.utils.Constant.getTimeAgo;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat.R;
import com.example.chat.databinding.AddUserBinding;
import com.example.chat.databinding.DrawerUserBinding;
import com.example.chat.inter.ClickListener;
import com.example.chat.model.User;
import com.example.chat.utils.Constant;

import java.util.ArrayList;
import java.util.List;

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
            case 3:
            case 4:
            case 5:
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
            case 3:
            case 4:
            case 5:
                bindAddUserViewHolder((AddUserViewHolder) holder, currentItem);
                break;
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    private void bindUserViewHolder(UserViewHolder holder, User currentItem) {
        DrawerUserBinding drawerUserBinding = holder.userBinding;
        if (type == 1) {
            drawerUserBinding.clickDrawerUserMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClick.onClickItem(currentItem, holder.getAdapterPosition(), type, 0);
                }
            });
            Glide.with(activity).load(Constant.getResource(currentItem.getUserPicture())).into(drawerUserBinding.drawerUserImage);

            drawerUserBinding.drawerUserActiveStatus.setVisibility(View.VISIBLE);
            drawerUserBinding.drawerUserActiveStatus.setImageResource(Constant.getUserActiveStatus(currentItem.getUserActiveStatus()));


        }
    }

    private void bindAddUserViewHolder(AddUserViewHolder holder, User currentItem) {
        AddUserBinding addUserBinding = holder.addUserBinding;
        if (type == 2) {
            addUserBinding.cancelClick.setVisibility(View.GONE);
            addUserBinding.acceptClick.setVisibility(View.GONE);
            addUserBinding.messageChatClick.setVisibility(View.GONE);
            addUserBinding.addRequestClick.setVisibility(View.VISIBLE);
            addUserBinding.messageChatClick.setVisibility(View.GONE);
            addUserBinding.addRequestClick.setEnabled(currentItem.isButtonEnabled());
            addUserBinding.allUserRequestTime.setVisibility(View.INVISIBLE);
            if (currentItem.isButtonEnabled() && !currentItem.isRequestSuccess()) {
                addUserBinding.addRequestTextClick.setVisibility(View.VISIBLE);
                addUserBinding.addRequestProgressClick.setVisibility(View.INVISIBLE);

                addUserBinding.addRequestTextClick.setTextColor(Color.parseColor("#b9babf"));
                addUserBinding.addRequestTextClick.setText("Add");
            }
            if (!currentItem.isButtonEnabled() && !currentItem.isRequestSuccess()) {
                addUserBinding.addRequestTextClick.setVisibility(View.INVISIBLE);
                addUserBinding.addRequestProgressClick.setVisibility(View.VISIBLE);

            }
            if (!currentItem.isButtonEnabled() && currentItem.isRequestSuccess()) {
                addUserBinding.addRequestProgressClick.setVisibility(View.INVISIBLE);
                addUserBinding.addRequestTextClick.setVisibility(View.VISIBLE);
                addUserBinding.addRequestTextClick.setTextColor(Color.parseColor("#57F287"));
                addUserBinding.addRequestTextClick.setText("Done");
            }
            addUserBinding.addRequestClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClick.onClickItem(currentItem, holder.getAdapterPosition(), type, 0);
                }
            });
        } else if (type == 3) {
            addUserBinding.allUserRequestTime.setVisibility(View.VISIBLE);
            addUserBinding.cancelClick.setVisibility(View.VISIBLE);
            addUserBinding.acceptClick.setVisibility(View.GONE);
            addUserBinding.addRequestClick.setVisibility(View.GONE);
            addUserBinding.cancelClick.setEnabled(currentItem.isButtonEnabled());
            addUserBinding.allUserRequestTime.setText(getTimeAgo(currentItem.getRequestTime()));
            if (currentItem.isButtonEnabled() && !currentItem.isRequestSuccess()) {
                addUserBinding.cancelImageClick.setVisibility(View.VISIBLE);
                addUserBinding.cancelImageClick.setImageResource(R.drawable.cancel);
                addUserBinding.cancelProgressClick.setVisibility(View.INVISIBLE);
            }


            if (!currentItem.isButtonEnabled() && !currentItem.isRequestSuccess()) {
                addUserBinding.cancelProgressClick.setVisibility(View.VISIBLE);
                addUserBinding.cancelImageClick.setVisibility(View.INVISIBLE);

            }


            if (!currentItem.isButtonEnabled() && currentItem.isRequestSuccess()) {
                addUserBinding.cancelImageClick.setVisibility(View.VISIBLE);
                addUserBinding.cancelImageClick.setImageResource(R.drawable.check);
                addUserBinding.cancelProgressClick.setVisibility(View.INVISIBLE);
            }
            addUserBinding.cancelClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClick.onClickItem(currentItem, holder.getAdapterPosition(), type, 0);
                }
            });
        } else if (type == 4) {
            addUserBinding.cancelClick.setVisibility(currentItem.isRequestSuccess() ? View.GONE : View.VISIBLE);
            addUserBinding.acceptClick.setVisibility(currentItem.isRequestSuccess() ? View.GONE : View.VISIBLE);
            addUserBinding.allUserRequestTime.setVisibility(View.VISIBLE);
            addUserBinding.allUserRequestTime.setText(getTimeAgo(currentItem.getRequestTime()));
            addUserBinding.messageChatClick.setVisibility(View.GONE);
            addUserBinding.cancelClick.setVisibility(currentItem.isButtonEnabled() ? View.VISIBLE : View.GONE);
            addUserBinding.acceptClick.setVisibility(currentItem.isButtonEnabled() ? View.VISIBLE : View.GONE);
            addUserBinding.cancelImageClick.setVisibility(currentItem.isButtonEnabled() ? View.VISIBLE : View.INVISIBLE);
            addUserBinding.acceptImageClick.setVisibility(currentItem.isButtonEnabled() ? View.VISIBLE : View.INVISIBLE);
            addUserBinding.cancelProgressClick.setVisibility(currentItem.isButtonEnabled() ? View.INVISIBLE : View.VISIBLE);
            addUserBinding.acceptProgressClick.setVisibility(currentItem.isButtonEnabled() ? View.INVISIBLE : View.VISIBLE);
            addUserBinding.cancelClick.setEnabled(currentItem.isButtonEnabled());
            addUserBinding.acceptClick.setEnabled(currentItem.isButtonEnabled());
            addUserBinding.addRequestClick.setVisibility(View.GONE);
            addUserBinding.acceptClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addUserBinding.cancelClick.setEnabled(false);
                    addUserBinding.acceptClick.setEnabled(false);
                    addUserBinding.acceptImageClick.setVisibility(View.INVISIBLE);
                    addUserBinding.acceptProgressClick.setVisibility(View.VISIBLE);
                    itemClick.onClickItem(currentItem, holder.getAdapterPosition(), type, 1);
                }
            });
            addUserBinding.cancelClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addUserBinding.cancelClick.setEnabled(false);
                    addUserBinding.acceptClick.setEnabled(false);
                    addUserBinding.cancelImageClick.setVisibility(View.INVISIBLE);
                    addUserBinding.cancelProgressClick.setVisibility(View.VISIBLE);
                    itemClick.onClickItem(currentItem, holder.getAdapterPosition(), type, 0);
                }
            });

        } else if (type == 5) {
            addUserBinding.messageChatClick.setVisibility(View.VISIBLE);
            addUserBinding.cancelClick.setVisibility(View.GONE);
            addUserBinding.acceptClick.setVisibility(View.GONE);
            addUserBinding.addRequestClick.setVisibility(View.GONE);
            addUserBinding.allUserRequestTime.setVisibility(View.VISIBLE);

            addUserBinding.allUserRequestTime.setText(getTimeAgo(currentItem.getRequestTime()));
            addUserBinding.messageChatClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClick.onClickItem(currentItem, holder.getAdapterPosition(), type, 0);
                }
            });
            addUserBinding.addUserNameAndUserId.setText(currentItem.getUserName() + "#" + String.valueOf(currentItem.getUserId()));
            addUserBinding.activeStatusLayout.setVisibility(View.VISIBLE);
            addUserBinding.addFriendsUserActiveStatus.setImageResource(Constant.getUserActiveStatusResource(currentItem.getUserActiveStatus()));
            addUserBinding.userRole.setVisibility(View.VISIBLE);
            addUserBinding.userRole.setImageResource(Constant.getUserActiveStatus(currentItem.getUserActiveStatus()));
        }
        Glide.with(activity).load(Constant.getResource(currentItem.getUserPicture())).into(addUserBinding.addUserPicture);
        addUserBinding.addUserDisplayName.setText(currentItem.getUserDisplayName());
        addUserBinding.allUserVerifiedId.setVisibility(currentItem.isUserVerified() ? View.VISIBLE : View.INVISIBLE);
        addUserBinding.activeStatusLayout.setVisibility(View.VISIBLE);
        addUserBinding.addFriendsUserActiveStatus.setImageResource(Constant.getUserActiveStatusResource(currentItem.getUserActiveStatus()));
        if (type == 2 || type == 3 || type == 4) {
            if (!currentItem.isUserSecurity()) {
                addUserBinding.addUserNameAndUserId.setText(currentItem.getUserName() + "#" + String.valueOf(currentItem.getUserId()));
                addUserBinding.userRole.setVisibility(View.VISIBLE);
                addUserBinding.userRole.setImageResource(Constant.getUserActiveStatus(currentItem.getUserActiveStatus()));
            } else {
                addUserBinding.addUserNameAndUserId.setText("");
                addUserBinding.userRole.setVisibility(View.INVISIBLE);
            }
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