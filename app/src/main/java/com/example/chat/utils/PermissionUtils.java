package com.example.chat.utils;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class PermissionUtils {
    public static final int REQUEST_READ_PHONE_STATE = 1;
    public static final int REQUEST_ACCESS_FINE_LOCATION = 3;
    public static final int REQUEST_STORAGE_PERMISSIONS = 4;
    public static boolean hasReadPhoneStatePermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestReadPhoneStatePermission(Fragment fragment) {
        fragment.requestPermissions(
                new String[]{Manifest.permission.READ_PHONE_STATE},
                REQUEST_READ_PHONE_STATE
        );
    }

    public static boolean hasAccessFineLocationPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestAccessFineLocationPermission(Fragment fragment) {
        fragment.requestPermissions(
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_ACCESS_FINE_LOCATION
        );
    }

    public static boolean hasStoragePermissions(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestStoragePermissions(Fragment fragment) {
        fragment.requestPermissions(
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                REQUEST_STORAGE_PERMISSIONS
        );
    }
}
