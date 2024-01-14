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

import com.example.chat.R;
import com.example.chat.databinding.FragmentLocationBinding;
import com.example.chat.databinding.FragmentSettingsBinding;
import com.example.chat.utils.Constant;
import com.example.chat.utils.DimensionUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationFragment extends Fragment implements View.OnClickListener{
    private FragmentLocationBinding binding;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Handler handler = new Handler();
    private Runnable runnable;
    int delay = 2000;
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
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLocations();
        } else {
            binding.openMap.setVisibility(View.INVISIBLE);
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }


    private void getLocations() {
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

                        binding.latitudeText.setText(String.valueOf( addresses.get(0).getLatitude()));
                        binding.longitudeText.setText(String.valueOf( addresses.get(0).getLongitude()));
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

                getLocations();

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
        if (view.getId()==binding.locationBackPressed.getId()) {
            requireActivity().onBackPressed();
        }
    }
}