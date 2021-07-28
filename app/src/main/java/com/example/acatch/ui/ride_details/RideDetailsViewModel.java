package com.example.acatch.ui.ride_details;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.acatch.model.Model;
import com.example.acatch.model.Ride;

import java.util.List;

public class RideDetailsViewModel extends ViewModel {
    private LiveData<List<Ride>> rideList;

    public RideDetailsViewModel() {
        rideList = Model.instance.getAllRides();
    }

    public LiveData<List<Ride>> getData() {
            return rideList;
    }


}