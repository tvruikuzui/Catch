package com.example.acatch.ui.map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.acatch.model.Ride;
import com.example.acatch.model.Ride;
import com.example.acatch.model.Model;

import java.util.List;

public class MapViewModel extends ViewModel {
    private LiveData<List<Ride>> rideList;

    public MapViewModel() {
        rideList = Model.instance.getAllRides();
    }

    public LiveData<List<Ride>> getData() {
            return rideList;
    }


}