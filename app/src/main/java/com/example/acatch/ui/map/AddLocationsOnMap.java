package com.example.acatch.ui.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.acatch.R;
import com.example.acatch.ui.new_ride.NewRide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;


public class AddLocationsOnMap extends Fragment implements OnMapReadyCallback {


    View view;
    MapViewModel mapViewModel;
    boolean isPermissionGranted;
    FusedLocationProviderClient client;
    GoogleMap mGoogleMap;
    FloatingActionButton zoom;
    Button saveLocationBtn;
    double srcLatitude = 0, srcLongitude = 0, desLatitude = 0 , desLongitude = 0;
    String title="";
    ImageView  deleteImgV;
    int saveWho = 1;
    TextView myLocationAsSourceTv, srcTv,desTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_locations_on_map, container, false);
        zoom = view.findViewById(R.id.addLocationOnMap_floatingActionButton);

        saveLocationBtn = view.findViewById(R.id.addLocationOnMap_save_btn);
        deleteImgV = view.findViewById(R.id.addLocationOnMap_delete_imgV);
        myLocationAsSourceTv = view.findViewById(R.id.addLocationOnMap_myLocationAsSource_tv);
        srcTv = view.findViewById(R.id.addLocationOnMap_sorce_tv);
        desTv = view.findViewById(R.id.addLocationOnMap_destenation_tv);

        deleteImgV.setOnClickListener(v-> {
            saveWho = 1;
            srcLatitude = 0;
            srcLongitude = 0;
            desLatitude = 0;
            desLongitude = 0;
            srcTv.setText("");
            desTv.setText("");
            mGoogleMap.clear();
        });

        mapViewModel  = new ViewModelProvider(this).
                get(MapViewModel.class);
        mapViewModel.getData().observe(getViewLifecycleOwner(), (data)-> {});


        checkPermission();

        initMap();

        client = new FusedLocationProviderClient(getActivity());

        zoom.setOnClickListener(v -> getCurrentFocus());
        myLocationAsSourceTv.setOnClickListener(v->addCurrentLocationAsSource());
        saveLocationBtn.setOnClickListener(v -> {
                NewRide.srcLat = srcLatitude;
                NewRide.srcLon= srcLongitude;
                NewRide.desLat = desLatitude;
                NewRide.desLon= desLongitude;
                Navigation.findNavController(view).navigateUp();
        });


        return view;
    }

    private void checkPermission() {
        Dexter.withContext(getContext()).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
//                Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                isPermissionGranted = true;
                initMap();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getContext().getPackageName(), "");
                intent.setData(uri);
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    private void addCurrentLocationAsSource() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful() && saveWho==1) {
                Location location = task.getResult();
                saveWho=2;
                title="Source";
                srcLatitude = location.getLatitude();
                srcLongitude = location.getLongitude();
                myLocationAsSourceTv.setText("My location as source added!");
                myLocationAsSourceTv.setTextColor(getResources().getColor(R.color.green));
                srcTv.setText(srcLatitude + "  " + srcLongitude);

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(title);
                mGoogleMap.addMarker(markerOptions);
            }
        });
    }

    private void getCurrentFocus() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Location location = task.getResult();
                goToLocation(location.getLatitude(), location.getLongitude());
            }
        });
    }

    private void goToLocation(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
//        LatLng latLng = new LatLng(32.013733,34.765637);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        mGoogleMap.moveCamera(cameraUpdate);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        mGoogleMap.addMarker(new MarkerOptions().position(latLng).title("My Location"));
        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)
                .radius(30) // radius in meters
                .fillColor(0x8800CCFF) //this is a half transparent blue, change "88" for the transparency
                .strokeColor(Color.BLUE) //The stroke (border) is blue
                .strokeWidth(2);
        mGoogleMap.addCircle(circleOptions);
    }

    private void initMap() {
        if (isPermissionGranted) {
            SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.addLocationOnMap_map);
            supportMapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {

                if(saveWho==1){
                    saveWho=2;
                    title="source";
                    srcLatitude = latLng.latitude;
                    srcLongitude = latLng.longitude;
                    srcTv.setText(srcLatitude + "  " + srcLongitude);
                    MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(title);
                    mGoogleMap.addMarker(markerOptions);
                }
                else if(saveWho==2){
                    saveWho=3;
                    title="Destination";
                    desLatitude = latLng.latitude;
                    desLongitude = latLng.longitude;
                    desTv.setText(desLatitude + "  " + desLongitude);
                    MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(title);
                    mGoogleMap.addMarker(markerOptions);
                }

//                if(saveWho==1 || saveWho==2)
//                {
//                    if(saveWho==2)
//                        saveWho=3;
//
//                    MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(title);
//                    mGoogleMap.addMarker(markerOptions);
//                }
            }
        });
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {

                return false;
            }
        });

    }
}