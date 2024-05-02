package com.example.chat.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.chat.databinding.AddUserBinding;
import com.example.chat.databinding.DrawerUserBinding;
import com.example.chat.databinding.GalleryImageLayoutBinding;
import com.example.chat.inter.ClickListener;
import com.example.chat.model.User;
import com.example.chat.utils.Constant;

import java.io.File;
import java.util.ArrayList;

public class GalleryCustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final Activity activity;
    private final ArrayList<String> imagePathList;
    private final ClickListener itemClick;
    private final int type;

    public GalleryCustomAdapter(Activity activity, ArrayList<String> imagePathList, ClickListener itemClick, int type) {
        this.activity = activity;
        this.imagePathList = imagePathList;
        this.itemClick = itemClick;
        this.type = type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (type) {
            case 1:
                return new GalleryCustomAdapter.GalleryImageViewHolder(GalleryImageLayoutBinding.inflate(inflater, parent, false));
            default:
                throw new IllegalArgumentException("Invalid view type");
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String currentItem = imagePathList.get(position);
        switch (type) {
            case 1:
                bindUserViewHolder((GalleryCustomAdapter.GalleryImageViewHolder) holder, currentItem);
                break;
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }
    private void bindUserViewHolder(GalleryCustomAdapter.GalleryImageViewHolder holder, String currentItem) {
        GalleryImageLayoutBinding imageBinding= holder.imageBinding;
        if(type==1){
            File imgFile = new File(currentItem);
            if (imgFile.exists()) {
                Glide.with(activity).load(imgFile.getPath()).into(imageBinding.imageGalleryViewID);
            }

        }
        // Add logic for binding UserViewHolder if needed
    }

    @Override
    public int getItemCount() {
        return imagePathList.size();
    }
    public static class GalleryImageViewHolder extends RecyclerView.ViewHolder {
        public final GalleryImageLayoutBinding imageBinding;

        public GalleryImageViewHolder(GalleryImageLayoutBinding imageBinding) {
            super(imageBinding.getRoot());
            this.imageBinding = imageBinding;
        }
    }

}
