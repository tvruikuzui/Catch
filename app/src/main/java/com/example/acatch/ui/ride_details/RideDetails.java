package com.example.acatch.ui.ride_details;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.acatch.R;
import com.example.acatch.model.Model;
import com.example.acatch.model.Ride;
import com.example.acatch.ui.login.LoginViewModel;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.UUID;


public class RideDetails extends Fragment {


    View view;
    ImageView image;
    Bitmap imageBitmap;
    TextView date,time,num,description,driverName;
    Button addBtn;
    ProgressBar progressBar;
    RideDetailsViewModel rideDetailsViewModel;
    int position;
    Ride ride;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_ride_details, container, false);
        image = view.findViewById(R.id.rideDetails_image_imgV);
        driverName = view.findViewById(R.id.rideDetails_drivername_tv);
        date = view.findViewById(R.id.rideDetails_date_et);
        time = view.findViewById(R.id.rideDetails_time_et);
        num = view.findViewById(R.id.rideDetails_num_et);
        description = view.findViewById(R.id.rideDetails_description_et);
        addBtn = view.findViewById(R.id.rideDetails_add_btn);
        image.setImageDrawable(getResources().getDrawable(R.drawable.car_avatar));
        progressBar = view.findViewById(R.id.newRide_progressBar);
        position = RideDetailsArgs.fromBundle(getArguments()).getPosition();

        rideDetailsViewModel = new ViewModelProvider(this).get(RideDetailsViewModel.class);
        rideDetailsViewModel.getData().observe(getViewLifecycleOwner(), (data)->{ initData(data); });

        addBtn.setOnClickListener(v->save());
        return view;
    }

    private void save() {
        ride.setNumSits((Integer.parseInt(ride.getNumSits()) - 1)+"");
        Ride newRide = new Ride();
        newRide.setId(UUID.randomUUID().toString());
        newRide.setCustomer(Model.instance.getUser().getId());
        newRide.setOwner(ride.getOwner());
        newRide.setNumSits(ride.getNumSits());
        newRide.setDriverName(ride.getDriverName());
        newRide.setDeleted(false);
        newRide.setTime(ride.getTime());
        newRide.setDate(ride.getDate());
        newRide.setDescription(ride.getDescription());
        newRide.setImage(ride.getImage());

        Model.instance.saveRide(newRide,(isSuccess)->{
            if(isSuccess){
                Model.instance.updateRide(ride,(isSuccess2)->{
                    while(Navigation.findNavController(view).popBackStack());
                    Navigation.findNavController(view).navigate(R.id.nav_myCatches);
                });
            }
//            else //drop some error to the user instead navigate to another page
//                Navigation.findNavController(view).navigate(R.id.nav_myCatches);
        });
    }

    private void initData(List<Ride> data) {
        ride = data.get(position);

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
}