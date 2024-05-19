package com.example.chat.ui.fragment.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chat.R;
import com.example.chat.databinding.FragmentLocationBinding;
import com.example.chat.databinding.FragmentSettingsBinding;
import com.example.chat.utils.Constant;
import com.example.chat.utils.DimensionUtils;
import com.example.chat.utils.NetworkUtils;
import com.example.chat.utils.PermissionUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationFragment extends Fragment implements View.OnClickListener {
    private FragmentLocationBinding binding;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Handler handler = new Handler();
    private Runnable runnable;
    private int delay = 2000;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLocationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Constant.setTopMargin(binding.locationFragmentMargin, DimensionUtils.getStatusBarHeight(requireActivity()));

        binding.deviceInformationText.setText(Constant.getSystemDetail(requireActivity()));
        binding.locationBackPressed.setOnClickListener(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        if (PermissionUtils.hasAccessFineLocationPermission(requireActivity())) {
            performActionWithLocationPermission();
        } else {
            binding.openMap.setVisibility(View.INVISIBLE);
            PermissionUtils.requestAccessFineLocationPermission(this);
        }


        // Check for READ_PHONE_STATE permission
        if (PermissionUtils.hasReadPhoneStatePermission(requireActivity())) {
            // Permission already granted, proceed with your code
            performActionWithPhoneStatePermission();
        } else {
            // Permission not granted, request it
            PermissionUtils.requestReadPhoneStatePermission(this);
        }
    }

    private void performActionWithPhoneStatePermission() {
        binding.networkConnectionText.setText(NetworkUtils.getConnectionQuality(requireActivity()));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermissionUtils.REQUEST_READ_PHONE_STATE) {
            // Check if the READ_PHONE_STATE permission was granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with your code
                toastMessage("Read Phone State Permission Granted");
                performActionWithPhoneStatePermission();
            } else {
                toastMessage("Read Phone State Permission Denied");
            }
        }
        if (requestCode == PermissionUtils.REQUEST_ACCESS_FINE_LOCATION) {
            // Check if the ACCESS_FINE_LOCATION permission was granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with your location-related code
                toastMessage("Location Permission Granted");
                performActionWithLocationPermission();
            } else {
                toastMessage("Location Permission Denied");
            }
        }
    }


    private void toastMessage(String message) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void performActionWithLocationPermission() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    Geocoder geocoder = new Geocoder(requireActivity(),
                            Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1
                        );

                        binding.latitudeText.setText(String.valueOf(addresses.get(0).getLatitude()));
                        binding.longitudeText.setText(String.valueOf(addresses.get(0).getLongitude()));
                        binding.countryNameText.setText(addresses.get(0).getCountryName());
                        binding.localityText.setText(addresses.get(0).getLocality());
                        binding.addressText.setText(addresses.get(0).getAddressLine(0));
                        binding.openMap.setVisibility(View.VISIBLE);
                        binding.openMap.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String strUri = "http://maps.google.com/maps?q=loc:" + addresses.get(0).getLatitude() + "," + addresses.get(0).getLongitude() + " (" + addresses.get(0).getAddressLine(0) + ")";
                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));

                                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

                                Log.d("okkk", strUri);

                                startActivity(intent);
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    @Override
    public void onResume() {
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);
                performActionWithPhoneStatePermission();
                performActionWithLocationPermission();

            }
        }, delay);
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); //stop handler when activity not visible super.onPause();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == binding.locationBackPressed.getId()) {
            requireActivity().onBackPressed();
        }
    }
}