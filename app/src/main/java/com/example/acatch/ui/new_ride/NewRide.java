package com.example.acatch.ui.new_ride;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.acatch.R;
import com.example.acatch.model.Model;
import com.example.acatch.model.Ride;

import java.util.Calendar;
import java.util.UUID;


public class NewRide extends Fragment {

    View view;
    ImageView image , camera, location;
    Bitmap imageBitmap;
    EditText driverName,num,description;
    TextView date, time;
    Button saveBtn;
    ProgressBar progressBar;
    public static double srcLat , srcLon, desLat, desLon;
    String dateToSave,timeToSave;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_new_ride, container, false);
        image = view.findViewById(R.id.newRide_image_imgV);
        camera = view.findViewById(R.id.newRide_camera_imgV);
        location = view.findViewById(R.id.newRide_location_imgV);
        driverName = view.findViewById(R.id.newRide_driverName_et);
        date = view.findViewById(R.id.newRide_date_et);
        time = view.findViewById(R.id.newRide_time_et);
        num = view.findViewById(R.id.newRide_num_et);
        description = view.findViewById(R.id.newRide_description_et);
        saveBtn = view.findViewById(R.id.newRide_save_btn);
        image.setImageDrawable(getResources().getDrawable(R.drawable.car_avatar));
        progressBar = view.findViewById(R.id.newRide_progressBar);
        driverName.setText(Model.instance.getUser().getFullName());
        driverName.setEnabled(false);
        
        camera.setOnClickListener(v->takePicture());
        saveBtn.setOnClickListener(v->saveRide());
        location.setOnClickListener(v-> Navigation.findNavController(v).navigate(R.id.nav_addLocationsOnMap));
        date.setOnClickListener(v->datePicker());
        time.setOnClickListener(v->timePicker());

        if(imageBitmap!=null)
            image.setImageBitmap(imageBitmap);


        return view;
    }

    private void timePicker() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                time.setText(selectedHour + ":" + selectedMinute);
                timeToSave = time.getText().toString();
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private void datePicker() {
        final Calendar c = Calendar.getInstance();
        int mYear, mMonth, mDay;
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog
                (
                    getContext(),
                    (DatePickerDialog.OnDateSetListener) (view, year, monthOfYear, dayOfMonth) -> {
                        date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        dateToSave = date.getText().toString();
                    },
                    mYear, mMonth, mDay
                );

        datePickerDialog.show();
    }

    private void saveRide() {

        progressBar.setVisibility(View.VISIBLE);
        if(imageBitmap!=null)
            Model.instance.uploadImage(imageBitmap,Model.instance.getUser().getId(), url -> save(url));
        else
            save(null);
    }

    void save(String url)
    {
        saveBtn.setEnabled(false);
        Ride ride = new Ride();

        if(url!=null)
            ride.setImage(url);
        else if(ride.getImage()==null || ride.getImage().equals(""))
            ride.setImage("");

        ride.setId(UUID.randomUUID().toString());
        ride.setCustomer("");
        ride.setOwner(Model.instance.getUser().getId());
        ride.setDriverName(driverName.getText().toString());
        ride.setDate(dateToSave);
        ride.setTime(timeToSave);
        ride.setDeleted(false);
        ride.setDescription(description.getText().toString());
        ride.setNumSits(num.getText().toString());
        ride.setSrcLatitude(srcLat);
        ride.setSrcLongitude(srcLon);
        ride.setDesLatitude(desLat);
        ride.setDesLongitude(desLon);


        Model.instance.saveRide(ride, (isSuccess)->{
            if(isSuccess) {
                while(Navigation.findNavController(view).popBackStack());
                Navigation.findNavController(view).navigate(R.id.nav_myRides);
            }
            else{
                progressBar.setVisibility(View.INVISIBLE);
                saveBtn.setEnabled(true);
            }
        });
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