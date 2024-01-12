package com.example.chat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.example.chat.R;
import com.example.chat.model.User;
import com.example.chat.model.ValidationResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Pattern;

public class Constant {

    public static final String TAG = "eee";
    public static final String USER = "user";
    public static final String START = "start";
    public static final String JOIN = "join";
    public static final String ALLUSERS = "AllUsers";
    public static final String GROUPS = "Group";
    public static final String ALLGROUPS = "AllGroup";
    public static final String UPDATEUSER = "updateStatus";
    public static final String UPDATEPROFILE = "UpdateProfile";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String GROUPNAME = "name";
    public static final String USERGROUP = "userGroup";
    public static final String TEXT = "Text";
    public static final String IMAGE = "image";
    public static final String MESSAGE = "message";
    public static final String SOURCE_ID = "source_id";
    public static final String DES_ID = "des_id";
    public static final String TYPE = "type";
    public static final String ISONLINE = "isOnline";

    private static final String DATA_LOGIN = "statusLogin";
    private static final String DATA_ID = "userId";
    private static final String DATA_KEY = "mainKey";
    private static final String DATA_PASS = "userPassword";

    public static final int[] TAB_ICONS = new int[]{
            R.drawable.users,
            R.drawable.chat,
            R.drawable.add_users
    };
    public static final String[] TAB_TEXTS = new String[]{
            "Users",
            "Home",
            "Add User"
    };

    public static void setDataAs(Context context, String userId,String mainKey, String userPassword, boolean status) {
        SharedPreferences.Editor editor = getSharePref(context).edit();
        editor.putString(DATA_ID, userId);
        editor.putString(DATA_PASS, userPassword);
        editor.putString(DATA_KEY, mainKey);
        editor.putBoolean(DATA_LOGIN, status);
        editor.apply();
    }
    public static ValidationResult isPasswordValid(String password) {
        Pattern specialCharPattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Pattern upperCasePattern = Pattern.compile("[A-Z ]");
        Pattern lowerCasePattern = Pattern.compile("[a-z ]");
        Pattern digitCasePattern = Pattern.compile("[0-9 ]");
        StringBuilder errorMessage = new StringBuilder();
        boolean isValid = true;

        if (password.length() < 8) {
            errorMessage.append("Password length must have at least 8 characters !!\n");
            isValid = false;
        }

        if (!specialCharPattern.matcher(password).find()) {
            errorMessage.append("Password must have at least one special character !!\n");
            isValid = false;
        }

        if (!upperCasePattern.matcher(password).find()) {
            errorMessage.append("Password must have at least one uppercase character !!\n");
            isValid = false;
        }

        if (!lowerCasePattern.matcher(password).find()) {
            errorMessage.append("Password must have at least one lowercase character !!\n");
            isValid = false;
        }

        if (!digitCasePattern.matcher(password).find()) {
            errorMessage.append("Password must have at least one digit character !!\n");
            isValid = false;
        }

        return new ValidationResult(isValid, errorMessage.toString());
    }
    public static String getDataId(Context context) {
        return getSharePref(context).getString(DATA_ID, "");
    }
    public static String getDataMainKey(Context context) {
        return getSharePref(context).getString(DATA_KEY, "");
    }
    public static String getDataPass(Context context) {
        return getSharePref(context).getString(DATA_PASS, "");
    }
    public static boolean getDataLogin(Context context) {
        return getSharePref(context).getBoolean(DATA_LOGIN, false);
    }
    public static void clearData(Context context) {
        SharedPreferences.Editor editor = getSharePref(context).edit();
        editor.remove(DATA_ID);
        editor.remove(DATA_PASS);
        editor.remove(DATA_LOGIN);
        editor.remove(DATA_KEY);
        editor.apply();
    }
    public static SharedPreferences getSharePref(Context context) {
        return context.getSharedPreferences("Share", Activity.MODE_PRIVATE);
    }
    public static boolean isValidBangladeshiPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^\\+?(88)?0?1[3456789]\\d{8}$");
    }
    public static boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }



    public static void getBitmapImage(Activity activity, Bitmap path, ImageView imageView) {
        Glide.with(activity)
                .asBitmap()
                .load(path)
                .into(imageView);
    }

    public static Bitmap decodeImage(String imagePath) {
        byte[] decodeString = android.util.Base64.decode(imagePath, android.util.Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
    }

    public static String convertToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        String image = android.util.Base64.encodeToString(
                byteArrayOutputStream.toByteArray(),
                android.util.Base64.DEFAULT
        );
        return image;
    }




    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list) {
        HashSet<T> uniqueElements = new HashSet<>(list);
        ArrayList<T> newList = new ArrayList<>(uniqueElements);

//        for (T element : list) {
//            if (uniqueElements.add(element)) {
//                // The element is added to the set (not a duplicate)
//                newList.add(element);
//            }
//        }

        return newList;
    }
}
