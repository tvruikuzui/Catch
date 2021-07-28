package com.example.acatch.ui.catch_or_give_ride;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.acatch.R;


public class CatchOrGiveRideFragment extends Fragment {


    View view;
    Button catchBtn , giveBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_catch_or_give_ride, container, false);
        catchBtn = view.findViewById(R.id.cathcOrGive_catch_btn);
        giveBtn = view.findViewById(R.id.cathcOrGive_give_btn);


        catchBtn.setOnClickListener(v-> Navigation.findNavController(v).navigate(R.id.nav_allRides));
        giveBtn.setOnClickListener(v-> Navigation.findNavController(v).navigate(R.id.nav_newRide));

        return view;
    }
}