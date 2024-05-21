package com.example.chat.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.provider.Settings;
import android.util.Patterns;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.chat.R;
import com.example.chat.adapter.UserAdapter;
import com.example.chat.model.User;
import com.example.chat.model.ValidationResult;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.regex.Matcher;
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
            R.drawable.add_user_down
    };
    public static final String[] TAB_TEXTS = new String[]{
            "Users",
            "Home",
            "Add User"
    };

    public static String getTimeAgo(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date pastDate = dateFormat.parse(dateString);
            long currentTime = System.currentTimeMillis();
            long timeDifference = currentTime - pastDate.getTime();
            if (timeDifference < 0) {
                return "In the future";
            }
            long seconds = timeDifference / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;
            long months = days / 30;
            long years = months / 12;
            if (years > 0) {
                return years == 1 ? "1y" : years + "y";
            } else if (months >= 1 && months <= 11) {
                return months == 1 ? "1mo" : months + "mo";
            } else if (days > 0) {
                return days == 1 ? "1d" : days + "d";
            } else if (hours > 0) {
                return hours == 1 ? "1h" : hours + "h";
            } else if (minutes > 0) {
                return minutes == 1 ? "1m" : minutes + "m";
            } else {
                return seconds == 1 ? "1s" : seconds + "s";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "Invalid date";
        }
    }


    public static void userUpdate(ArrayList<User> userList, UserAdapter userAdapter, int type) {
        ArrayList<User> existingData = userAdapter.getData();
        ArrayList<User> newUserIds = new ArrayList<>();
        int existingIndex, insertionIndex;
        for (User user : userList) {
            existingIndex = binarySearch(existingData, user.getUserId());
            newUserIds.add(user);
            if (existingIndex == -1) {
                insertionIndex = findInsertionIndex(existingData, user.getUserId());
                existingData.add(insertionIndex, user);
                userAdapter.notifyItemInserted(insertionIndex);
            } else {
                User existingUser = existingData.get(existingIndex);

                if (type == 1) {
                    if (!existingUser.UserEqual(user)) {
                        existingData.set(existingIndex, user);
                        userAdapter.notifyItemChanged(existingIndex);
                    }
                } else if (type == 2) {
                    if (!existingUser.friendUserEqual(user)) {
                        existingData.set(existingIndex, user);
                        userAdapter.notifyItemChanged(existingIndex);
                    }
                }

            }
        }
        for (int i = existingData.size() - 1; i >= 0; i--) {
            User existingUser = existingData.get(i);
            int position = binarySearch(newUserIds, existingUser.getUserId());
            if (position == -1) {
                existingData.remove(i);
                userAdapter.notifyItemRemoved(i);
            }
        }

    }

    public static Object getResource(String userPicture) {
        switch (userPicture) {
            case "1":
                return R.drawable.frame1;
            case "2":
                return R.drawable.frame2;
            case "3":
                return R.drawable.frame3;
            case "4":
                return R.drawable.frame4;
            case "5":
                return R.drawable.frame5;
            case "6":
                return R.drawable.frame6;
            case "7":
                return R.drawable.frame7;
            case "8":
                return R.drawable.frame8;
            case "null":
                return R.drawable.users;
            default:
                return userPicture;
        }
    }


    public static int getUserActiveStatusResource(String userPicture) {
        switch (userPicture) {
            case "Online":
                return R.drawable.online;
            case "Idle":
                return R.drawable.idle;
            case "DND":
                return R.drawable.dnd;
            case "Invisible":
                return R.drawable.invisible;
        }
        return R.drawable.circular_border;
    }

    public static int getUserActiveStatus(String userPicture) {
        switch (userPicture) {
            case "Online":
                return R.drawable.circular_border_green;
            case "Idle":
                return R.drawable.circular_border_yellow;
            case "DND":
                return R.drawable.circular_border_red;
            case "Invisible":
                return R.drawable.circular_border_ash;
        }
        return R.drawable.circular_border;
    }

    public static void introSort(ArrayList<User> list) {
        int maxDepth = (int) (2 * Math.log(list.size()) / Math.log(2));
        introSort(list, 0, list.size() - 1, maxDepth);
    }

    private static void introSort(ArrayList<User> list, int low, int high, int maxDepth) {
        if (low < high) {
            if (maxDepth == 0)
                heapSort(list, low, high);
            else {
                int pivotIndex = partition(list, low, high);
                introSort(list, low, pivotIndex, maxDepth - 1);
                introSort(list, pivotIndex + 1, high, maxDepth - 1);
            }
        } else
            insertionSort(list, low, high);
    }

    private static void heapSort(ArrayList<User> list, int low, int high) {
        int n = high - low + 1;
        for (int i = n / 2 - 1; i >= 0; i--)
            heapify(list, n, i, low);
        for (int i = n - 1; i >= 0; i--) {
            Collections.swap(list, low, low + i);
            heapify(list, i, 0, low);
        }
    }

    private static void heapify(ArrayList<User> list, int n, int i, int low) {
        int largest = i;
        int leftChild = 2 * i + 1;
        int rightChild = 2 * i + 2;
        if (leftChild < n && list.get(low + leftChild).getUserId() > list.get(low + largest).getUserId())
            largest = leftChild;
        if (rightChild < n && list.get(low + rightChild).getUserId() > list.get(low + largest).getUserId())
            largest = rightChild;
        if (largest != i) {
            Collections.swap(list, low + i, low + largest);
            heapify(list, n, largest, low);
        }
    }

    private static int partition(ArrayList<User> list, int low, int high) {
        User pivot = list.get(low);
        int i = low - 1;
        int j = high + 1;
        while (true) {
            do
                i++;
            while (list.get(i).getUserId() < pivot.getUserId());
            do
                j--;
            while (list.get(j).getUserId() > pivot.getUserId());
            if (i >= j)
                return j;
            Collections.swap(list, i, j);
        }
    }

    private static void insertionSort(ArrayList<User> list, int low, int high) {
        for (int i = low + 1; i <= high; i++) {
            User key = list.get(i);
            int j = i - 1;
            while (j >= low && list.get(j).getUserId() > key.getUserId()) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
        }
    }

    public static int findInsertionIndex(ArrayList<User> list, long targetUserId) {
        int low = 0;
        int high = list.size() - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (list.get(mid).getUserId() == targetUserId)
                return mid;
            else if (list.get(mid).getUserId() < targetUserId)
                low = mid + 1;
            else
                high = mid - 1;
        }
        return low;
    }

    public static int binarySearch(ArrayList<User> list, long target) {
        int low = 0;
        int high = list.size() - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (list.get(mid).getUserId() == target)
                return mid;
            else if (list.get(mid).getUserId() < target)
                low = mid + 1;
            else
                high = mid - 1;
        }
        return -1;
    }


    public static void setDataAs(Context context, String userId, String mainKey, String userPassword, boolean status) {
        SharedPreferences.Editor editor = getSharePref(context).edit();
        editor.putString(DATA_ID, userId);
        editor.putString(DATA_PASS, userPassword);
        editor.putString(DATA_KEY, mainKey);
        editor.putBoolean(DATA_LOGIN, status);
        editor.apply();
    }

    public static void setTopMargin(ViewGroup viewGroup, int topMargin) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) viewGroup.getLayoutParams();
        params.topMargin = topMargin;
        viewGroup.setLayoutParams(params);
    }

    public static void setBottomMargin(ViewGroup viewGroup, int bottomMargin) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) viewGroup.getLayoutParams();
        params.bottomMargin = bottomMargin;
        viewGroup.setLayoutParams(params);
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

    @SuppressLint("HardwareIds")
    public static String getSystemDetail(Context context) {
        return "Brand: " + Build.BRAND + "\n" +
                "DeviceID: " + Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID) + "\n" +
                "Model: " + Build.MODEL + "\n" +
                "ID: " + Build.ID + "\n" +
                "SDK: " + Build.VERSION.SDK_INT + "\n" +
                "Manufacture: " + Build.MANUFACTURER + "\n" +
                "Brand: " + Build.BRAND + "\n" +
                "User: " + Build.USER + "\n" +
                "Type: " + Build.TYPE + "\n" +
                "Base: " + Build.VERSION_CODES.BASE + "\n" +
                "Incremental: " + Build.VERSION.INCREMENTAL + "\n" +
                "Board: " + Build.BOARD + "\n" +
                "Host: " + Build.HOST + "\n" +
                "FingerPrint: " + Build.FINGERPRINT + "\n" +
                "Version Code: " + Build.VERSION.RELEASE;
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
