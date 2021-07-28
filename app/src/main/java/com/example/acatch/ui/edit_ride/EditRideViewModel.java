package com.example.acatch.ui.edit_ride;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.acatch.model.Model;
import com.example.acatch.model.Ride;

import java.util.LinkedList;
import java.util.List;

public class EditRideViewModel extends ViewModel {


    private LiveData<List<Ride>> ridesList;
    public List<Ride> list;

    public EditRideViewModel() {
        ridesList = Model.instance.getAllRides();
        list = new LinkedList<>();
    }

    public LiveData<List<Ride>> getData() {
        return ridesList;
    }

}