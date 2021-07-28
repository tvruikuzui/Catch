package com.example.acatch.ui.my_catches;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.acatch.model.Model;
import com.example.acatch.model.Ride;
import com.example.acatch.model.User;

import java.util.LinkedList;
import java.util.List;

public class MyCatchesViewModel extends ViewModel {


    private LiveData<List<Ride>> ridesList;
    public List<Ride> list;

    public MyCatchesViewModel() {
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
            if(r.getCustomer().equals(Model.instance.getUser().getId()))
                list.add(r);
        }

        return list;
    }

    public Ride getCurrentRide(Ride customerRide) {
        Ride ride = null;
        if(ridesList.getValue()!=null){
            for (Ride r: ridesList.getValue()) {
                if(customerRide.getCustomer().equals(r.getCustomer())
                        && customerRide.getDate().equals(r.getDate())
                        && customerRide.getTime().equals(r.getTime())){
                    ride = r;
                    break;
                }
            }
        }
        return ride;
    }

    public Ride getOwnerRide(Ride customerRide) {
        Ride ride = null;
        if(ridesList.getValue()!=null){
            for (Ride r: ridesList.getValue()) {
                if(customerRide.getOwner().equals(r.getOwner()) &&
                        r.getCustomer().equals("") &&
                        customerRide.getDate().equals(r.getDate()) &&
                        customerRide.getTime().equals(r.getTime()))
                {
                    ride = r;
                    break;
                }
            }
        }
        return ride;
    }
}