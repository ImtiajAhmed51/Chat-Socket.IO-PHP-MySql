package com.example.chat.ui.fragment.auth;

import static com.example.chat.utils.Constant.isValidBangladeshiPhoneNumber;
import static com.example.chat.utils.Constant.isValidEmail;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.Manifest;

import com.bumptech.glide.Glide;
import com.example.chat.R;
import com.example.chat.databinding.FragmentChooseProfilePictureBinding;
import com.example.chat.ui.activity.MainActivity;
import com.example.chat.ui.design.ProgressButton;
import com.example.chat.utils.BackgroundWorker;
import com.example.chat.utils.Constant;
import com.example.chat.utils.EncryptionUtils;
import com.example.chat.utils.PermissionUtils;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.view.CropImageView;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ChooseProfilePictureFragment extends Fragment implements View.OnClickListener {

    private FragmentChooseProfilePictureBinding binding;
    private String userId = "", mainVal, pass;
    private ProgressButton progressButton;
    private static final int PICK_IMAGE = 1;
    private String selectedImage = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChooseProfilePictureBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeArguments();
        setClickListeners(
                binding.ProfileImageCardView1,
                binding.ProfileImageCardView2,
                binding.ProfileImageCardView3,
                binding.ProfileImageCardView4,
                binding.ProfileImageCardView5,
                binding.ProfileImageCardView6,
                binding.ProfileImageCardView7,
                binding.ProfileImageCardView8
        );
        progressButton = new ProgressButton(requireActivity(), binding.saveChooseProfilePictureClick);
        progressButton.buttonSet("Next");
        binding.saveChooseProfilePictureClick.setOnClickListener(this);
        binding.skipClick.setOnClickListener(this);
        binding.addUserPicture.setOnClickListener(this);
    }
    private void initializeArguments() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            userId = arguments.getString("userId");
            mainVal = arguments.getString("mainVal");
            pass = arguments.getString("pass");
        }
    }
    @Override
    public void onClick(View view) {
        int selectedImageResource = 0;
        if (view.getId() == binding.ProfileImageCardView1.getId()) {
            selectedImage = "1";
            selectedImageResource = R.drawable.frame1;
        } else if (view.getId() == binding.ProfileImageCardView2.getId()) {
            selectedImage = "2";
            selectedImageResource = R.drawable.frame2;
        } else if (view.getId() == binding.ProfileImageCardView3.getId()) {
            selectedImage = "3";
            selectedImageResource = R.drawable.frame3;
        } else if (view.getId() == binding.ProfileImageCardView4.getId()) {
            selectedImage = "4";
            selectedImageResource = R.drawable.frame4;
        } else if (view.getId() == binding.ProfileImageCardView5.getId()) {
            selectedImage = "5";
            selectedImageResource = R.drawable.frame5;
        } else if (view.getId() == binding.ProfileImageCardView6.getId()) {
            selectedImage = "6";
            selectedImageResource = R.drawable.frame6;
        } else if (view.getId() == binding.ProfileImageCardView7.getId()) {
            selectedImage = "7";
            selectedImageResource = R.drawable.frame7;
        } else if (view.getId() == binding.ProfileImageCardView8.getId()) {
            selectedImage = "8";
            selectedImageResource = R.drawable.frame8;
        } else if (view.getId() == binding.saveChooseProfilePictureClick.getId()) {
            if (selectedImage != null) {
                binding.saveChooseProfilePictureClick.setClickable(false);
                progressButton.buttonActivated();
                BackgroundWorker backgroundWorker = new BackgroundWorker(this::processChangePictureResponse);
                try {
                    backgroundWorker.execute("ChangePicture", EncryptionUtils.encrypt(userId), selectedImage);
                } catch (Exception e) {
                    toastMessage(e.getMessage());
                }
            }


        } else if (view.getId() == binding.skipClick.getId()) {
            selectedImage = null;
            binding.skipClick.setClickable(false);
            login();
        } else if (view.getId() == binding.addUserPicture.getId()) {
            pickImageFromGallery();
        }

        if (view instanceof CardView && view.getId() != binding.addUserPicture.getId()) {

            resetBackgroundColors();

            ((CardView) view).setCardBackgroundColor(getResources().getColor(R.color.purple_500));
            binding.userProfileImage.setImageResource(selectedImageResource);
        }
    }

    private String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return Base64.encodeToString(baos.toByteArray(), 0);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == -1 && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                startCrop(imageUri);
            }
        } else if (requestCode == 69 && resultCode == -1) {
            Uri imageUriResultCrop = UCrop.getOutput(data);
            if (imageUriResultCrop != null) {
                Bitmap image = uriToBitmap(requireActivity(), imageUriResultCrop);
                Bitmap image2 = getResizedBitmap(image, CropImageView.DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION);
                Glide.with(requireActivity()).load(image2).into(binding.userProfileImage);
                selectedImage = bitmapToString(image2);
                resetBackgroundColors();
            }
        }
    }

    public static Bitmap uriToBitmap(Context context, Uri uri) {
        try {
            ContentResolver resolver = context.getContentResolver();
            InputStream inputStream = resolver.openInputStream(uri);
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            Log.e("ImageUtils", "Error converting Uri to Bitmap: " + e.getMessage());
            return null;
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int height;
        int width;
        float bitmapRatio = ((float) image.getWidth()) / ((float) image.getHeight());
        if (bitmapRatio > 1.0f) {
            width = maxSize;
            height = (int) (((float) width) / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (((float) height) * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private void startCrop(Uri uri) {
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(requireActivity().getCacheDir(), "SampleCropImg" + ".jpg")));
        uCrop.withAspectRatio(1.0f, 1.0f);
        uCrop.withMaxResultSize(8000, 8000);
        uCrop.withOptions(getCropOptions());
        uCrop.start(requireActivity(), this, 69);
    }

    private UCrop.Options getCropOptions() {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(100);
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(false);
        options.setStatusBarColor(getResources().getColor(R.color.purple_500));
        options.setToolbarColor(getResources().getColor(R.color.purple_500));
        options.setToolbarWidgetColor(getResources().getColor(R.color.white));
        options.setCropFrameColor(getResources().getColor(R.color.purple_500));
        options.setActiveControlsWidgetColor(getResources().getColor(R.color.teal_200));
        options.setToolbarTitle("Crop Image");
        return options;
    }
    public void pickImageFromGallery() {
        if (PermissionUtils.hasStoragePermissions(requireActivity())) {
            // Permissions already granted, proceed with your code
            startImagePicker();
        } else {
            // Permissions not granted, request them
            PermissionUtils.requestStoragePermissions(this);
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
    private void startImagePicker() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        startActivityForResult(pickIntent, PICK_IMAGE);
    }
    private void login() {
        BackgroundWorker backgroundWorker = new BackgroundWorker(this::processLoginResponse);
        try {
            String emailParam = isValidEmail(mainVal) ? EncryptionUtils.encrypt(mainVal) : EncryptionUtils.encrypt("");
            String phoneNumberParam = isValidBangladeshiPhoneNumber(mainVal) ? EncryptionUtils.encrypt(mainVal) : EncryptionUtils.encrypt("");
            backgroundWorker.execute("Login", emailParam, phoneNumberParam, EncryptionUtils.encrypt(pass));
        } catch (Exception e) {
            if(selectedImage != null){
                binding.saveChooseProfilePictureClick.setClickable(true);
                progressButton.buttonSet("Next");
            }
            binding.skipClick.setClickable(true);
            toastMessage(e.getMessage());
        }
    }
    private void processChangePictureResponse(Object output) {
        try {
            JSONObject jsonResponse = new JSONObject((String) output);
            if (jsonResponse.getBoolean("success")) {
                login();
            } else {
                binding.saveChooseProfilePictureClick.setClickable(true);
                progressButton.buttonSet("Next");
                toastMessage(jsonResponse.getString("message"));
            }
        } catch (Exception e) {
            if(selectedImage != null){
                binding.saveChooseProfilePictureClick.setClickable(true);
                progressButton.buttonSet("Next");
            }
            toastMessage(e.getMessage());
        }
    }
    private void processLoginResponse(Object output) {
        try {
            JSONObject jsonResponse = new JSONObject((String) output);
            if (jsonResponse.getBoolean("success")) {
                if (selectedImage != null) {
                    progressButton.buttonFinished();
                }
                Constant.setDataAs(requireActivity(), EncryptionUtils.decrypt(jsonResponse.getString("userId")), mainVal, pass, true);
                startMainActivity(jsonResponse.toString());
            } else {
                if (selectedImage != null) {
                    binding.saveChooseProfilePictureClick.setClickable(true);
                    progressButton.buttonSet("Next");
                }
                Navigation.findNavController(requireView()).navigate(R.id.action_chooseProfilePictureFragment_to_welcomeFragment);
                toastMessage(jsonResponse.getString("message"));
            }
        } catch (Exception e) {
            if (selectedImage != null) {
                binding.saveChooseProfilePictureClick.setClickable(true);
                progressButton.buttonSet("Next");
            }
            binding.skipClick.setClickable(true);
            toastMessage(e.getMessage());
        }
    }
    private void startMainActivity(String jsonObject) {
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.putExtra("jsonObject", jsonObject);
        startActivity(intent);
        requireActivity().finish();
    }
    private void resetBackgroundColors() {
        CardView[] cardViews = {
                binding.ProfileImageCardView1,
                binding.ProfileImageCardView2,
                binding.ProfileImageCardView3,
                binding.ProfileImageCardView4,
                binding.ProfileImageCardView5,
                binding.ProfileImageCardView6,
                binding.ProfileImageCardView7,
                binding.ProfileImageCardView8
        };
        for (CardView cardView : cardViews) {
            cardView.setCardBackgroundColor(getResources().getColor(R.color.transparent));
        }
    }
    private void toastMessage(String message) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
    }
    private void setClickListeners(CardView... cardViews) {
        for (CardView cardView : cardViews) {
            cardView.setOnClickListener(this);
        }
    }
}