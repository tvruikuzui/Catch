package com.example.acatch.ui.my_rides;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.acatch.model.Model;
import com.example.acatch.model.Ride;

import java.util.LinkedList;
import java.util.List;

public class MyRidesViewModel extends ViewModel {


    private LiveData<List<Ride>> ridesList;
    public List<Ride> list;

    public MyRidesViewModel() {
        ridesList = Model.instance.getAllRides();
        list = new LinkedList<>();
    }

    public LiveData<List<Ride>> getData() {
        return ridesList;
    }

    public void refresh(){
        Model.instance.getAllRides();
    }

    public List<Ride> filterByUser(List<Ride> data) {
        list = new LinkedList<>();
        for (Ride r: data) {
            if(r.getOwner().equals(Model.instance.getUser().getId()) && r.getCustomer().equals(""))
                list.add(r);
        }

        return list;
    }

    public List<Ride> getRidesToDelete(Ride rideToDelete) {

        List<Ride> ridesToDeleteList = new LinkedList<>();

        if(ridesList.getValue()!=null)
        {
            for (Ride r: ridesList.getValue())
            {
                if(rideToDelete.getOwner().equals(r.getOwner())
                        && rideToDelete.getDate().equals(r.getDate())
                        && rideToDelete.getTime().equals(r.getTime()))
                {
                    ridesToDeleteList.add(r);
                }
            }
        }
        return ridesToDeleteList;
    }
}