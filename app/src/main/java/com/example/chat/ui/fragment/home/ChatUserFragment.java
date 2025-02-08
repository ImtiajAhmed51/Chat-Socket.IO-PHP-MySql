package com.example.chat.ui.fragment.home;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chat.adapter.GalleryCustomAdapter;
import com.example.chat.databinding.FragmentChatUserBinding;
import com.example.chat.inter.ClickListener;
import com.example.chat.model.User;
import com.example.chat.utils.Constant;
import com.example.chat.utils.PermissionUtils;

import java.util.ArrayList;

public class ChatUserFragment extends Fragment implements View.OnClickListener, ClickListener {
    private FragmentChatUserBinding binding;

    private ArrayList<String> pictureList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatUserBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle b = requireActivity().getIntent().getExtras();
        binding.chatUserDisplayName.setText(b.getString("userDisplayName"));
        binding.chatUserVerified.setVisibility(b.getBoolean("userVerified") ? View.VISIBLE : View.GONE);
        Glide.with(requireActivity()).load(Constant.getResource(b.getString("userPicture"))).into(binding.chatUserImage);
        binding.chatUserActiveStatus.setImageResource(Constant.getUserActiveStatus(b.getString("userActiveStatus")));
        b.getString("userDisplayName");

        binding.chatUserBackPressed.setOnClickListener(this);


        binding.pickImageGallery.setOnClickListener(this);
    }

    private void toastMessage(String message) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == binding.pickImageGallery.getId()) {
            if (binding.imageViewGallery.getVisibility() == View.GONE) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        binding.imageViewGallery.setVisibility(View.VISIBLE);
                    }
                }, 20);
            }
            pickImageFromGallery();
        } else if (v.getId()==binding.chatUserBackPressed.getId()) {
            requireActivity().onBackPressed();

        }
    }

    public void pickImageFromGallery() {
        if (PermissionUtils.hasStoragePermissions(requireActivity())) {
            // Permissions already granted, proceed with your code
            getPicturePaths();
        } else {
            // Permissions not granted, request them
            PermissionUtils.requestStoragePermissions(this);
        }
        prepareRecyclerView();
    }

    private void prepareRecyclerView() {
        if (requireActivity().getCurrentFocus() != null) {
            ((InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);
        }
        GalleryCustomAdapter galleryCustomAdapter = new GalleryCustomAdapter(requireActivity(), pictureList, this, 1);

        binding.recyclerGalleryImageView.setHasFixedSize(true);
        binding.recyclerGalleryImageView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        binding.recyclerGalleryImageView.setAdapter(galleryCustomAdapter);
        galleryCustomAdapter.notifyItemRangeChanged(0, pictureList.size());
        if (pictureList.size() != 0) {
            binding.messagePicture.setVisibility(View.GONE);
            binding.countPicture.setText("Photos: " + String.valueOf(pictureList.size()));
            return;
        }
        binding.messagePicture.setVisibility(View.VISIBLE);
        binding.messagePicture.setText("Do not have any picture in your phone.");
    }

    private void getPicturePaths() {

        String[] projection = {MediaStore.Images.Media.DATA};
        String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " DESC";
        Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        pictureList.clear();

        try (Cursor cursor = requireActivity().getContentResolver().query(
                queryUri,
                projection,
                null,
                null,
                sortOrder
        )) {
            if (cursor != null) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                while (cursor.moveToNext()) {
                    String picturePath = cursor.getString(columnIndex);
                    pictureList.add(picturePath);
                }
                cursor.close();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtils.REQUEST_STORAGE_PERMISSIONS) {
            // Check if both permissions were granted
            if (grantResults.length > 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                toastMessage("Permission to access storage is required to pick an image.");
                pickImageFromGallery();
            } else {
                toastMessage("Permission to access storage is required to pick an image.");
            }
        }
    }

    @Override
    public void onClickItem(User user, int position, int type, int buttonType) {

    }

    @Override
    public void onClickGalleryImage(int position, int type) {

    }
}