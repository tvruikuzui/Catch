package com.example.acatch.ui.edit_ride;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.acatch.R;
import com.example.acatch.model.Model;
import com.example.acatch.model.Ride;
import com.example.acatch.ui.my_rides.MyRidesViewModel;
import com.squareup.picasso.Picasso;

import java.util.List;


public class EditRide extends Fragment {

    View view;
    EditRideViewModel editRideViewModel;
    String rideID;
    Ride ride;

    ImageView image;
    Bitmap imageBitmap;
    TextView date,time,num,description,driverName;
    ProgressBar progressBar;
    Button saveBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_edit_ride, container, false);
        image = view.findViewById(R.id.editRide_image_imgV);
        driverName = view.findViewById(R.id.editRide_driverName_et);
        date = view.findViewById(R.id.editRide_date_et);
        time = view.findViewById(R.id.editRide_time_et);
        num = view.findViewById(R.id.editRide_num_et);
        description = view.findViewById(R.id.editRide_description_et);
        progressBar = view.findViewById(R.id.editRide_progressBar);
        saveBtn = view.findViewById(R.id.editRide_save_btn);


        rideID = EditRideArgs.fromBundle(getArguments()).getRideID();
        editRideViewModel  = new ViewModelProvider(this).get(EditRideViewModel.class);
        editRideViewModel.getData().observe(getViewLifecycleOwner(), (data)->{ initData(data); });
        saveBtn.setOnClickListener(v->saveRide());

        return view;
    }

    private void saveRide() {

        progressBar.setVisibility(View.VISIBLE);
        if(imageBitmap!=null)
            Model.instance.uploadImage(imageBitmap,rideID, url -> save(url));
        else
            save(null);
    }

    private void save(String url) {
        if(ride!=null) {
            saveBtn.setEnabled(false);

            ride.setDriverName(driverName.getText().toString());
            ride.setNumSits(num.getText().toString());
            ride.setTime(time.getText().toString());
            ride.setDate(date.getText().toString());
            ride.setDescription(description.getText().toString());

            if(url!=null)
                ride.setImage(url);
            else if(ride.getImage()==null || ride.getImage().equals(""))
                ride.setImage("");

            Model.instance.updateRide(ride, (isSuccess) -> {
                if (isSuccess) {
                    Model.instance.updateRide(ride, (isSuccess2) -> {
                        while(Navigation.findNavController(view).popBackStack());
                        Navigation.findNavController(view).navigate(R.id.nav_myRides);
                    });
                }
                else
                    saveBtn.setEnabled(true);
            });
        }
    }

    private void initData(List<Ride> data) {
        if(data!=null)
        {
            for (Ride r : data)
            {
                if (r.getId().equals(rideID)) {
                    ride = r;
                    break;
                }
            }
        }

        image.setImageResource(R.drawable.car_avatar);
        if(ride.getImage()!=null && !ride.getImage().equals("")){
            Picasso.get().load(ride.getImage()).placeholder(R.drawable.car_avatar).into(image);
        }
        driverName.setText(ride.getDriverName());
        date.setText(ride.getDate());
        time.setText(ride.getTime());
        num.setText(ride.getNumSits());
        description.setText(ride.getDescription());
    }

    //--------------------------------------------image from camera-------------------------------------

    static final int REQUEST_IMAGE_CAPTURE = 1;
    final static int RESULT_SUCCESS = -1;

    void takePicture()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //If we back from camera:
        if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode == RESULT_SUCCESS)
        {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            image.setImageBitmap(imageBitmap);
        }
    }
}